package bookstore.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry; // 新增這行
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Autowired
	private LoginInterceptor loginInterceptor;

	@Value("${file.upload-dir}")
	private String uploadDir;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**") 
				.allowedOrigins("http://localhost:5173") 
				.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") 
				.allowCredentials(true) 
				.allowedHeaders("*");
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(loginInterceptor)
				.addPathPatterns("/users/**")
				.excludePathPatterns("/login", "/api/login")
				.excludePathPatterns("/api/**")
				.excludePathPatterns("/logout")
				.excludePathPatterns("/static/**", "/css/**", "/js/**", "/images/**");
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/upload-images/**")
				.addResourceLocations("file:" + uploadDir);
	}
}