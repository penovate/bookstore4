package bookstore.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bookstore.bean.BooksBean;
import bookstore.bean.Cart;
import bookstore.exceptionCenter.BusinessException;
import bookstore.repository.BookRepository;
import bookstore.repository.CartRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private BookRepository bookRepository;

    public Cart addToCart(Integer userId, Integer bookId, Integer quantity) {
        BooksBean book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BusinessException(400, "找不到該書籍"));

        // Handle nulls safely
        int stock = (book.getStock() != null) ? book.getStock() : 0;
        int onShelf = (book.getOnShelf() != null) ? book.getOnShelf() : 0;

        if (onShelf != 1) {
            // Assuming 1 is On Shelf based on frontend logic.
            // If strict check needed: throw Exception or ignore.
            // Requirement says "失效商品管理...禁止勾選結帳", maybe add is ok but warn?
            // But for "Add to Cart", usually we block invalid items.
            throw new BusinessException(400, "該書籍目前未上架");
        }

        if (quantity > stock) {
            throw new BusinessException(400, "庫存不足，僅剩 " + stock + " 本");
        }

        Optional<Cart> existingCart = cartRepository.findByUserIdAndBookId(userId, bookId);

        Cart cart;
        if (existingCart.isPresent()) {
            cart = existingCart.get();
            int newQuantity = cart.getQuantity() + quantity;
            if (newQuantity > stock) {
                throw new BusinessException(400,
                        "加入後數量將超過庫存，目前購物車已有 " + cart.getQuantity() + " 本，庫存僅剩 " + stock + " 本");
            }
            cart.setQuantity(newQuantity);
            cart.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        } else {
            cart = new Cart(userId, bookId, quantity);
            cart.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            cart.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        }

        return cartRepository.save(cart);
    }

    public Cart updateQuantity(Integer cartId, Integer quantity) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new BusinessException(400, "找不到該購物車項目"));

        BooksBean book = bookRepository.findById(cart.getBookId())
                .orElseThrow(() -> new BusinessException(400, "找不到該書籍"));

        int stock = (book.getStock() != null) ? book.getStock() : 0;

        if (quantity > stock) {
            throw new BusinessException(400, "庫存不足，僅剩 " + stock + " 本");
        }

        if (quantity <= 0) {
            throw new BusinessException(400, "數量必須大於 0");
        }

        cart.setQuantity(quantity);
        cart.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return cartRepository.save(cart);
    }

    public void removeFromCart(Integer cartId) {
        cartRepository.deleteById(cartId);
    }

    public List<Cart> getUserCart(Integer userId) {
        List<Cart> cartItems = cartRepository.findByUserId(userId);

        // Auto-adjust quantities if they exceed stock
        for (Cart item : cartItems) {
            BooksBean book = item.getBooksBean();
            if (book != null) {
                int stock = (book.getStock() != null) ? book.getStock() : 0;
                if (item.getQuantity() > stock) {
                    if (stock > 0) {
                        item.setQuantity(stock);
                        item.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                        cartRepository.save(item);
                    } else {
                        // Optional: If stock is 0, we could leave it as is (user sees it's invalid)
                        // or set to 0? Usually we don't set cart qty to 0 unless removing.
                        // User request: "如果超過要先將購物車該筆書本數量改為實際庫存量"
                        // If stock is 0, qty becomes 0? That technically effectively removes it
                        // visually or shows 0.
                        // But cart item with 0 qty might handle weirdly.
                        // Let's set to stock (0) if stock is 0, but user said "change to actual stock".
                        // Use case: User added 5, stock became 3 -> set to 3.
                        // Use case: User added 1, stock became 0 -> set to 0.
                        // Frontend likely filters out invalid items or shows them as OOS.
                        // Let's safe guard to set to stock.
                        item.setQuantity(stock);
                        item.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                        cartRepository.save(item);
                    }
                }
            }
        }

        return cartItems;
    }

    public void clearCart(Integer userId) {
        cartRepository.deleteByUserId(userId);
    }
}
