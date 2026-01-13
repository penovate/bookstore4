package bookstore.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                Claims claims = jwtUtil.getClaims(token);
                String role = (String) claims.get("role");
                String uri = request.getRequestURI();

                if (uri.contains("/api/data/admin-only")) {
                    if (!"SUPER_ADMIN".equals(role)) {
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, "權限不足");
                        return false;
                    }
                }

                if (uri.contains("/api/data/")) {
                    if ("SUPER_ADMIN".equals(role) || "ADMIN".equals(role)) {
                        return true;
                    } else {
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, "您無權操作後台 API");
                        return false;
                    }
                }

                return true;
            } catch (Exception e) {
                System.out.println("Token 驗證失敗: " + e.getMessage());
            }
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
    }
}