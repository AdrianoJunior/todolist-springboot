package tech.codecraftsman.todolist.filter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tech.codecraftsman.todolist.user.IUserRepository;

import java.io.IOException;
import java.util.Base64;


@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


        var servletPath = request.getServletPath();

        if (servletPath.startsWith("/tasks/")) {
            var auth = request.getHeader("Authorization");

            var authEncoded = auth.substring("Basic".length()).trim();

            byte[] authDecoded = Base64.getDecoder().decode(authEncoded);

            String authString = new String(authDecoded);

            String[] credentials = authString.split(":");

            String username = credentials[0];
            String password = credentials[1];
            var user = this.userRepository.findByUsername(username);
            if (user != null) {
                var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());

                if(passwordVerify.verified) {
                    request.setAttribute("userId", user.getUid());
                    filterChain.doFilter(request, response);
                } else {
                    response.sendError(401);
                }

            } else {
                response.sendError(401);
            }

        } else {
            filterChain.doFilter(request, response);
        }


    }
}
