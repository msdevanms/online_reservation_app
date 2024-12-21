package com.tcs.authservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;

@RestController
@RequestMapping("/api/v1")
public class MainRestController {

    private static final Logger log = LoggerFactory.getLogger(MainRestController.class);

    @Value("${instance.id}")
    private String instance_id;

    CredentialRepository credentialRepository;
   AuthtokenRepository authtokenRepository;
   Producer producer;

   MainRestController(CredentialRepository credentialRepository,
                      AuthtokenRepository authtokenRepository,
                      Producer producer)
   {
       this.credentialRepository = credentialRepository;
       this.authtokenRepository = authtokenRepository;
       this.producer = producer;
   }

   @GetMapping("/get/instance/id")
   public ResponseEntity<String> getInstanceId()
   {
       log.info("INSTANCE ID REQUESTED");
       return ResponseEntity.ok(instance_id);
   }

    @PostMapping("/signup")
    public ResponseEntity<Credential> signup(@RequestParam("username") String username,
                                             @RequestParam("password") String password) throws JsonProcessingException {
        Credential credential = new Credential();
        credential.setUsername(username);
        credential.setPassword(password);
        credentialRepository.save(credential);
        credential.setPassword("*********");

        producer.publishAuthDatum(username,"NEW SIGNUP");

        return ResponseEntity.ok(credential);
    }

    @PostMapping("/login") // THE TOKEN IS ONLY GENERATED HERE AND NOT VALIDATED
    public ResponseEntity<Authentication> login(
                        @RequestParam("username") String username,
                        @RequestParam("password") String password
                       ) throws JsonProcessingException {
        Credential credential = credentialRepository.findById(username).orElse(null);
        if (credential != null && credential.getPassword().equals(password))
        {
            Authentication loxAuthentication = new Authentication();

            Authtoken authtoken = new Authtoken();
            authtoken.setUsername(username);
            authtoken.setExpirytime(300);
            authtoken.setToken(String.valueOf(((int) (Math.random()*1000000))));
            authtoken.setCreationtime(Instant.now());
            authtokenRepository.save(authtoken);


            loxAuthentication.setAuthenticated(true);
            loxAuthentication.setAuthtoken(authtoken);
            loxAuthentication.setMessage("LOGIN SUCCESSFUL");

            producer.publishAuthDatum(username,"LOGIN SUCCESSFUL");

            return ResponseEntity.ok(loxAuthentication);
        }
        else
        {
            Authentication loxAuthentication = new Authentication();
            loxAuthentication.setAuthenticated(false);
            loxAuthentication.setMessage("INVALID CREDENTIALS");

            producer.publishAuthDatum(username,"LOGIN UNSUCCESSFUL");

            return ResponseEntity.status(HttpStatusCode.valueOf(401)).body(loxAuthentication);
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<Authtoken> validate(
            @RequestHeader("Sectoken") String token
    ) throws JsonProcessingException {

        log.info("TOKEN VALIDATION STARTED...");

        Authtoken authtoken = authtokenRepository.findById(token).orElse(null);
        if (authtoken != null)
        {
            // CHECK THE EXPIRY
            Duration duration = Duration.between(authtoken.getCreationtime(), Instant.now());
            if(duration.getSeconds() > authtoken.getExpirytime())
            {
                authtokenRepository.deleteById(token);

                Authtoken badAuthToken = new Authtoken();
                badAuthToken.setCreationtime(Instant.MAX);
                badAuthToken.setUsername("INVALIDUSER");
                badAuthToken.setExpirytime(0);
                badAuthToken.setToken("TOKENEXPIRED");
                log.info("TOKEN EXPIRED");

                producer.publishAuthDatum(authtoken.getUsername(),"EXPIRED TOKEN");

                return ResponseEntity.status(HttpStatusCode.valueOf(401)).body(badAuthToken);

            }

            producer.publishAuthDatum(authtoken.getUsername(), "TOKEN VALIDATED");

            log.info("TOKEN VALIDATED SUCCESSFULY"+authtoken);
            return ResponseEntity.ok(authtoken);
        }
        else
        {
            Authtoken badAuthToken = new Authtoken();
            badAuthToken.setCreationtime(Instant.MAX);
            badAuthToken.setUsername("INVALIDUSER");
            badAuthToken.setExpirytime(0);
            badAuthToken.setToken("INVALIDTOKEN");
            log.info("TOKEN INVALID");

            producer.publishAuthDatum("","INVALID TOKEN");

            return ResponseEntity.status(HttpStatusCode.valueOf(401)).body(badAuthToken);
        }
    }

}