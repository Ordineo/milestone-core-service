package be.ordina.ordineo.controller;

import be.ordina.ordineo.exception.EntityNotFoundException;
import be.ordina.ordineo.model.Milestone;
import be.ordina.ordineo.repository.MilestoneRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

/**
 * Created by shbe on 27/05/16.
 */


@Slf4j

@RestController
//@RepositoryRestController
@RequestMapping("/api/milestones")
public class MilestoneRestController {

    private List<String> authorities ;
    @Autowired
    private MilestoneRepository milestoneRepository;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @RequestMapping(value="/{id}",method = RequestMethod.GET)//, produces = "application/json"
   // public ResponseEntity<PersistentEntityResource> requestMilestone(@PathVariable Long id,
                                                                     //PersistentEntityResourceAssembler persistentEntityResourceAssembler){

        public ResponseEntity<Milestone> requestMilestone(@PathVariable Long id){

        log.info("inside http get method");
        Milestone milestone = milestoneRepository.findOne(id);
        //Validate.notNull(milestone);
        if(milestone == null){
            throw new EntityNotFoundException("Milestone not Found!");
        }
        return new ResponseEntity<Milestone>(milestone,HttpStatus.OK);// this should be made based on REst doc
        //return ok(persistentEntityResourceAssembler.toResource(milestone));

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @RequestMapping(value="/{id}",method = RequestMethod.PUT)
    public ResponseEntity UpdateMilestone(@PathVariable Long id,@RequestBody Milestone milestone,HttpServletRequest request) {
   // public PersistentEntityResource UpdateMilestone(@PathVariable Long id,@RequestBody Milestone milestone,HttpServletRequest request
       // ,PersistentEntityResourceAssembler persistentEntityResourceAssembler) {
       log.info("id of object which should be modified :" + id);
        try {
            Validate.notNull(milestone.getUsername());
            Milestone originalMilestone = milestoneRepository.findOne(id);//original Object
            Validate.notNull(originalMilestone);

            if (checkAuthorizationOfAuthenticatedUser(originalMilestone.getUsername())) {
                milestone.setId(originalMilestone.getId());
                milestone = milestoneRepository.save(milestone);



               HttpHeaders httpHeaders = new HttpHeaders();
               log.info("----------------------   "+ request.getRequestURI());
                log.info("----------------------   "+ request.getRequestURL());

                URI uri = new URI(request.getRequestURL().toString());


                httpHeaders.setLocation(uri);

                return new ResponseEntity(null,httpHeaders,HttpStatus.NO_CONTENT);
               // return  persistentEntityResourceAssembler.toResource(milestone);*/
               // return persistentEntityResourceAssembler.toResource(milestone);
            }
        }catch(IllegalArgumentException ex) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        } catch (URISyntaxException e) {
            e.printStackTrace();// what should i put here???????
        }
        return new ResponseEntity(HttpStatus.FORBIDDEN);// is it enough or needs exception?????*/

        //return null;
    }
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @RequestMapping(value="/",method = RequestMethod.POST)//     /api/milestones
    public ResponseEntity createMilestone(@RequestBody Milestone milestone){
   // public PersistentEntityResource createMilestone(@RequestBody Milestone milestone,
                                                  // PersistentEntityResourceAssembler persistentEntityResourceAssembler ){
        log.info("id of object which should be created :" + milestone);
        Validate.notNull(milestone);
        if(checkAuthorizationOfAuthenticatedUser(milestone.getUsername())){
            milestone = milestoneRepository.save(milestone);
            return new ResponseEntity( HttpStatus.CREATED);
           // return persistentEntityResourceAssembler.toResource(milestone);
        }
        return new ResponseEntity(HttpStatus.FORBIDDEN);// is it enough or needs exception?????
        //return null;
    }
  /*  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @RequestMapping(value="/{id}",method = RequestMethod.DELETE)// for now Delete is not allowed
    public ResponseEntity deleteMilestone(){

        return new ResponseEntity(HttpStatus.METHOD_NOT_ALLOWED);
    }*/

    /*@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @RequestMapping(value="/api/milestones/{id}",method = RequestMethod.GET)
    public void milestoneView(@PathVariable String userName){
        List<Milestone> milestones = milestoneRepository.findByUsernameOrderByDate(userName);
        if()
    }*/
   /* @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")//search/findByUsername?{userName}{value}
    @RequestMapping(value="/search/findByUsername?username={username}",method = RequestMethod.GET)// /search/findByUsername?{userName}{value}
    public ResponseEntity<List<Milestone>> milestoneView(@PathVariable String username){
        log.info("----------------------------  :> "+username );
       // List<Milestone> milestones = milestoneRepository.findByUsernameOrderByDate(userName);
       // return new ResponseEntity<>(milestones,HttpStatus.OK); // i should make the json format
        return new ResponseEntity<>(HttpStatus.OK);
    }*/






    private boolean checkAuthorizationOfAuthenticatedUser(String userName){
        boolean userIsTheOwner = false;
        authorities = getRoles();
      if(userName != null && !userName.isEmpty()
              && !authorities.isEmpty() && authorities !=null) {
          if (authorities.contains("ROLE_ADMIN")
                  ||SecurityContextHolder.getContext().getAuthentication().getName().equals(userName)
                   ) {
              userIsTheOwner = true;
          }
      }
        return userIsTheOwner;
    }
    private List<String> getRoles(){
        authorities = new ArrayList<String>();
        if(SecurityContextHolder.getContext().getAuthentication()!=null){
            log.info("roles " + SecurityContextHolder.getContext().getAuthentication().getAuthorities()
            );

            for(GrantedAuthority grantedAuthority: SecurityContextHolder.getContext().getAuthentication().getAuthorities()){
                log.info(grantedAuthority+"\n");
                authorities.add(grantedAuthority.toString().trim());
            }
        }
        return authorities;
    }
}
