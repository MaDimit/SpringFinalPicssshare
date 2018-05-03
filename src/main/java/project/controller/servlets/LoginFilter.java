package project.controller.servlets;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(filterName = "LoginFilter", urlPatterns = {"/index.html", "/editprofile.html", "/upload.html"})
public class LoginFilter implements  Filter{

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        HttpSession session = request.getSession(false);

        String path = request.getRequestURI();
        boolean loggedIn = session != null && session.getAttribute("user") != null;
        boolean loginRequest = (path.startsWith("/login.html") || path.startsWith("/user/register") || path.startsWith("/user/login") || path.startsWith("/util/recovery/password"));

        if (loggedIn || loginRequest || path.endsWith(".css") || path.endsWith(".js")) {
            chain.doFilter(req, resp);
        } else {
            response.sendRedirect("login.html");
        }
    }

    @Override
    public void destroy() {

    }


}