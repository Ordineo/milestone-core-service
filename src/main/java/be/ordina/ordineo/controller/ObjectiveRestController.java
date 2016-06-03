
package be.ordina.ordineo.controller;

import be.ordina.ordineo.exception.EntityNotFoundException;
import be.ordina.ordineo.model.Objective;
import be.ordina.ordineo.repository.ObjectiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Created by shbe on 02/06/16.
 */

@Slf4j
@RestController
@RequestMapping("/api/objectives")
public class ObjectiveRestController {
    @Autowired
    ObjectiveRepository objectiveRepository;
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @RequestMapping(value="/{id}", method = RequestMethod.GET)
    public ResponseEntity<Object> requestObjective(@PathVariable Long id){
        log.info("inside Get method for Objective");
        Validate.notNull(id);
        Objective objective = objectiveRepository.findOne(id);
        if(objective == null){
            throw new EntityNotFoundException("Objective Not Found!");
        }
        Resource rs = new Resource(objective);
        rs.add((linkTo(methodOn(ObjectiveRestController.class).requestObjective(id)).withSelfRel()));
        return new ResponseEntity<Object>(rs, HttpStatus.OK);
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')") // for now just Admin can  update Objective
    @RequestMapping(value="/{id}", method = RequestMethod.PUT)
    public ResponseEntity updateObjective(@PathVariable @Valid Long id , @RequestBody @Valid Objective objective,HttpServletRequest request)
            throws URISyntaxException {
        log.info("----- mehtod put for objective -------");
        Validate.notEmpty(objective.getTitle());
        Objective objectiveOriginal = objectiveRepository.findOne(id);
        Validate.notNull(objectiveOriginal);
        objective.setId(objectiveOriginal.getId());
        objectiveRepository.save(objective);
        HttpHeaders httpHeaders = new HttpHeaders();
        URI uri = new URI(request.getRequestURL().toString());
        httpHeaders.setLocation(uri);
        return new ResponseEntity(null,httpHeaders,HttpStatus.NO_CONTENT);
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')") // for now just Admin can create Objective
    @RequestMapping(value="/", method = RequestMethod.POST)
    public ResponseEntity createObjective(@RequestBody @Valid Objective objective, HttpServletRequest request)
            throws URISyntaxException {
        log.info("mehtod post for objective");
        Validate.notEmpty(objective.getTitle());
        objective = objectiveRepository.save(objective);
        HttpHeaders httpHeaders = new HttpHeaders();
        URI uri = null;
        uri = new URI(request.getRequestURL().append(objective.getId()).toString());
        httpHeaders.setLocation(uri);
        return new ResponseEntity(httpHeaders,HttpStatus.CREATED);
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')") // for now the mehtod is not allowed
    @RequestMapping(value="/{id}", method = RequestMethod.DELETE)
    public  ResponseEntity deleteObjective(@PathVariable Long id){
        return new ResponseEntity(HttpStatus.METHOD_NOT_ALLOWED);
    }
    // authorization for custom methods
   @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @RequestMapping(value="/search/findByTitleOrTags", params = {"text"}, method=RequestMethod.GET)
    public ResponseEntity<List<Objective>> findByTitleOrTags(@RequestParam(value = "text") String text ){
       log.info("inside mehtod Get for Objective find by Title");
       Validate.notEmpty(text);
       List<Objective> objectives = new ArrayList<Objective>();
       objectives = objectiveRepository.findByTitleOrTags(text);
       //ResponseEntity<List<Objective>> list = new ResponseEntity<List<Objective>>();
      /* for(Objective ob:objectives){
           Resource rs = new Resource(ob);
           rs.add((linkTo(methodOn(ObjectiveRestController.class).findByTitleOrTags(text)).withSelfRel()));
           list.add
       }*/
       //ResponseEntity<List<Objective>> objectives = findByTitleOrTags(text);
      // for(Objective ob : objectives)


        return null;
    }
   /*  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
  @RequestMapping(value="/{id}*" , method=RequestMethod.GET)
     public  ResponseEntity findByTitleOrTags(@PathVariable Long id ,HttpServletRequest httpServletRequest){
        log.info("sfdfsfsdfsdfdsfdsf");
         httpServletRequest.getRequestURI()
        return null;
    }*/
}
