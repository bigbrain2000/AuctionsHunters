package com.auctions.hunters.security;

import com.auctions.hunters.service.user.UserDetailsServiceImpl;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder.bCryptPasswordEncoder());
        return authProvider;
    }

    @Override
    protected void configure(@NotNull AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider());
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        SimpleUrlAuthenticationSuccessHandler successHandler = new SimpleUrlAuthenticationSuccessHandler();
        successHandler.setUseReferer(false);
        successHandler.setDefaultTargetUrl("/");

        return successHandler;
    }

    @Override
    protected void configure(@NotNull HttpSecurity http) throws Exception {

        String[] allUsersPermittedApis = {"/css/**", "/", "/login", "/login_error", "/logout", "/seller/register", "/buyer/register", "/confirm/**", "/seller/username"
        , "/images", "/images/**", "/images2", "/seller/cars/*"
        };
        String[] sellerPermittedApis = {"/seller/username"};
        String[] buyerPermittedApis = {};
        String[] authenticatedUsersPermittedApis = {"/homepage"};

        http
                .csrf().disable()
                .headers().frameOptions().disable()
                .httpStrictTransportSecurity().disable()
                .and()
                .authorizeRequests()
                //.authorizeRequests().antMatchers("/").hasAnyAuthority("ADMIN")
                .antMatchers(allUsersPermittedApis).permitAll()
//                .and()
//                .authorizeRequests().antMatchers(sellerPermittedApis).hasAnyAuthority("SELLER")
//                .and()
//                .authorizeRequests().antMatchers(buyerPermittedApis).hasAnyAuthority("BUYER")
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .successHandler(authenticationSuccessHandler())
                .failureUrl("/login_error")
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)        // set invalidation state when logout
                .deleteCookies("JSESSIONID").permitAll();
    }
}