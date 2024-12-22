package com.tcs.profileservice;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("api/v1")
public class MainRestController
{

    private static final Logger log = LoggerFactory.getLogger(MainRestController.class);


    UserdetailRepository userdetailRepository;
    WebClient webClient_1;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    MainRestController(UserdetailRepository userdetailRepository,
                       WebClient webClient_1)
    {
        this.userdetailRepository = userdetailRepository;
        this.webClient_1 = webClient_1;
    }

    @PostMapping("update/user/details")
    public ResponseEntity<String> updateUserDetails(
            @RequestHeader("Sectoken") String secToken,
            @RequestBody Userdetail userdetail,
            HttpServletRequest request,
            HttpServletResponse httpResponse)
    {
        // CHECK FOR THE SECRET COOKIE TO DECIDE WHETHER THIS IS A FRESH REQUEST OR A FOLLOW-UP REQUEST

        //STEP 0A: EXTRACT THE COOKIES FROM THE INCOMING REQUEST
        List<Cookie> cookieList = null;
        //Optional<String> healthStatusCookie = Optional.ofNullable(request.getHeader("health_status_cookie"));
        Cookie[] cookies = request.getCookies();
        if(cookies == null)
        {
            cookieList = new ArrayList<>();
        }
        else
        {
            // REFACTOR TO TAKE NULL VALUES INTO ACCOUNT
            cookieList = List.of(cookies);
        }

        if(cookieList.isEmpty() || cookieList.stream().filter(cookie -> cookie.getName().contains("UPUSDE")).findAny().isEmpty())
        {
            // IT's  FRESH REQUEST

            AtomicReference<String> username = new AtomicReference<>(null);

            if(secToken == null || secToken.isEmpty())
            {
                return ResponseEntity.badRequest().body(null);
            }
            else
            {

                //STEP 0B: CREATE A TEMPORARY COOKIE -- UPUSDE

                Cookie cookieUPUSDE = new Cookie("UPUSDE"+secToken, null); // STEP 3A
                cookieUPUSDE.setMaxAge(300);
                // Send the token for validation to Auth-Service in an Async manner

                Mono<Authtoken> responseAuth = webClient_1.post().header("Sectoken",secToken)
                        .retrieve()
                        .bodyToMono(Authtoken.class); // SENDING OUT AN ASYNCHRONOUS REQUEST

                // THE CODE ABOVE AND BELOW WILL BE EXECUTED AT DIFFERENT TIMES AND IN SEPERATE THREADS

                responseAuth.subscribe( // HANDLER FOR THE EVENTUAL RESPONSE TO THE ABOVE REQUEST
                        response -> {
                            log.info(response+" VALID TOKEN RESPONSE FROM AUTH-SERVICE");
                            username.set(response.getUsername());
                            redisTemplate.opsForValue().set(cookieUPUSDE.getName(), "OK");

                            if(username.get().equals(userdetail.getUsername()))
                            {
                                if(userdetailRepository.findById(username.get()).isEmpty())
                                {
                                    userdetailRepository.save(userdetail);
                                    log.info("USER DETAILS SAVED FOR "+username.get());
                                }
                                else
                                {
                                    userdetailRepository.updateFirstnameAndLastnameAndEmailAndPhoneAndCityAndCountryByUsername(
                                            userdetail.getFirstname(),
                                            userdetail.getLastname(),
                                            userdetail.getEmail(),
                                            userdetail.getPhone(),
                                            userdetail.getCity(),
                                            userdetail.getCountry(),
                                            username.get()
                                    );
                                    log.info("USER DETAILS UPDATED FOR "+username.get());
                                }

                            }
                            else
                            {
                                log.info("USER CREDENTIAL AND DETAILS MISMATCH "+username.get());
                            }
                        },
                        error ->
                        {
                            redisTemplate.opsForValue().set(cookieUPUSDE.getName(), "NOTOK");
                            log.info("INVALID TOKEN RESPONSE "+error);
                        });

                httpResponse.addCookie(cookieUPUSDE);
                redisTemplate.opsForValue().set(String.valueOf(cookieUPUSDE.getName()), cookieUPUSDE.getValue());
                return ResponseEntity.ok().body("AUTHENTICATING REQUEST... PLEASE CHECK AGAIN MR FRONTEND");

            }

        }else
        {
            //!!!POTENTIAL BUG BECAUSE OF IMPRECISE CHECK FOR COOKIE NAME
            Cookie cookieUPUSDE =  cookieList.stream().filter(cookie -> cookie.getName().contains("UPUSDE")).findFirst().get();

           if(redisTemplate.opsForValue().get(cookieUPUSDE.getName()) == null)
           {
               return ResponseEntity.ok().body("STILL AUTHENTICATING REQUEST... PLEASE CHECK AGAIN MR FRONTEND");
           }
           else
           {
                if(redisTemplate.opsForValue().get(cookieUPUSDE.getName()).equals("OK"))
                {
                    return ResponseEntity.ok().body("AUTHENTICATION SUCCESSFUL | USER DETAILS UPDATED");
                }
                else {
                    return ResponseEntity.ok().body("AUTHENTICATION FAILED");
                }
           }
        }




        }






}