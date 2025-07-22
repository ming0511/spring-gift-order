package gift.member.security;

import gift.member.Role;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = "/admin/*")
public class AdminJwtAuthenticationFilter implements Filter {

    private final JwtTokenProvider jwtTokenProvider;
    private final static String AUTHORIZATION_HEADER = "Authorization";
    private final static String BEARER_PREFIX = "Bearer ";

    public AdminJwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
        FilterChain filterChain)
        throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        String authorizationHeader = httpServletRequest.getHeader(AUTHORIZATION_HEADER);

        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                "Authorization 헤더가 없거나 형식이 잘못되었습니다.");
            return;
        }

        String token = authorizationHeader.substring(BEARER_PREFIX.length());

        if (!jwtTokenProvider.validateToken(token)) {
            httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 토큰입니다.");
            return;
        }

        Role role = jwtTokenProvider.getRoleFromToken(token);

        if (!Role.ADMIN.equals(role)) {
            httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "관리자 권한이 없습니다.");
            return;
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
