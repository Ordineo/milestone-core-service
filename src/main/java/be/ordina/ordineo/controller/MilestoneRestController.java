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
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
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

        public ResponseEntity<Object> requestMilestone(@PathVariable Long id){

        log.info("inside http get method");
        Milestone milestone = milestoneRepository.findOne(id);
        //Validate.notNull(milestone);
        if(milestone == null){
            throw new EntityNotFoundException("Milestone not Found!");
        }
        Resource rs = new Resource(milestone);
        rs.add((linkTo(methodOn(MilestoneRestController.class).requestMilestone(id)).withSelfRel()));
        return new ResponseEntity<Object>(rs, HttpStatus.OK);// this should be made based on REst doc



    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @RequestMapping(value="/{id}",method = RequestMethod.PUT)
    public ResponseEntity UpdateMilestone(@PathVariable Long id,@RequestBody @Valid Milestone milestone,HttpServletRequest request) throws URISyntaxException {
   // public PersistentEntityResource UpdateMilestone(@PathVariable Long id,@RequestBody Milestone milestone,HttpServletRequest request
       // ,PersistentEntityResourceAssembler persistentEntityResourceAssembler) {
       log.info("id of object which should be modified :" + id);

            Validate.notEmpty(milestone.getUsername());
            Milestone originalMilestone = milestoneRepository.findOne(id);//original Object
            Validate.notNull(originalMilestone);

            if (checkAuthorizationOfAuthenticatedUser(originalMilestone.getUsername())) {
                milestone.setId(originalMilestone.getId());
                milestone = milestoneRepository.save(milestone);
                HttpHeaders httpHeaders = new HttpHeaders();
                URI uri = new URI(request.getRequestURL().toString());
                httpHeaders.setLocation(uri);

                return new ResponseEntity(null,httpHeaders,HttpStatus.NO_CONTENT);
            }

        throw new AccessDeniedException("Access Forbidden!");


    }
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @RequestMapping(value="/",method = RequestMethod.POST)//     /api/milestones
    public ResponseEntity createMilestone(@RequestBody @Valid Milestone milestone,HttpServletRequest request) throws URISyntaxException {

        log.info("id of object which should be created :" + milestone);
        Validate.notEmpty(milestone.getUsername());
        if(checkAuthorizationOfAuthenticatedUser(milestone.getUsername())){
            milestone = milestoneRepository.save(milestone);
            URI uri = null;
            HttpHeaders httpHeaders = new HttpHeaders();
            uri = new URI(request.getRequestURL().append(milestone.getId()).toString());
            httpHeaders.setLocation(uri);
            return new ResponseEntity( httpHeaders,HttpStatus.CREATED);

        }
        throw new AccessDeniedException("Access Forbidden!");

    }
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @RequestMapping(value="/{id}",method = RequestMethod.DELETE)// for now Delete is not allowed
    public ResponseEntity deleteMilestone(){

        return new ResponseEntity(HttpStatus.METHOD_NOT_ALLOWED);
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @RequestMapping(value="/search/findByUsername", params = {"username"}, method = RequestMethod.GET)
    public ResponseEntity<Object> findByUsernameOrderByDate(@RequestParam(value = "username") @Valid String  username){
        log.info("inside method get findbname");
        List<Milestone> milestones = milestoneRepository.findByUsernameOrderByDate(username);
        List<Resource> resources = new ArrayList<>();
        for(Milestone ms: milestones){
            Resource rs = new Resource(ms);
            rs.add((linkTo(methodOn(MilestoneRestController.class).findByUsernameOrderByDate(username)).withSelfRel()));
            resources.add(rs);
        }

        return new ResponseEntity<>(resources,HttpStatus.OK);
    }

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
