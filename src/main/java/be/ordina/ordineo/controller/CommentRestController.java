package be.ordina.ordineo.controller;

import be.ordina.ordineo.exception.EntityNotFoundException;
import be.ordina.ordineo.model.Comment;
import be.ordina.ordineo.model.Milestone;
import be.ordina.ordineo.repository.CommentRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpHeaders;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.Path;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Created by shbe on 01/06/16.
 */

@Slf4j
@RestController
@RequestMapping("/api/comments")
@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
public class CommentRestController {
    @Autowired
    CommentRepository commentRepository;

   @RequestMapping(value="/{id}",method = RequestMethod.GET)//,produces = "application/hal+json"
   public ResponseEntity<Object> requestComment(@PathVariable Long id){
       log.info("inside Get request for Comment");
       Comment comment = commentRepository.findOne(id);
       if(comment == null){
           throw new EntityNotFoundException("Comment Not Found!");
       }
       Resource rs = new Resource(comment);
       rs.add((linkTo(methodOn(CommentRestController.class).requestComment(id)).withSelfRel()));
       return new ResponseEntity<Object>(rs,HttpStatus.OK);
   }

    @RequestMapping(value="/",method = RequestMethod.POST)//,produces = "application/hal+json"
    public ResponseEntity createComment(@RequestBody @Valid Comment comment,HttpServletRequest request) throws URISyntaxException {
        log.info("inside Post for comment");
        Validate.notEmpty(comment.getUsername());
        if(checkAuthorizationOfAuthenticatedUser(comment.getUsername())){
            comment = commentRepository.save(comment);
            HttpHeaders httpHeaders = new HttpHeaders();
            URI uri = null;
            uri = new URI(request.getRequestURL().append(comment.getId()).toString());
            httpHeaders.setLocation(uri);
            return new ResponseEntity(httpHeaders,HttpStatus.CREATED);
        }

        throw new AccessDeniedException("Access Forbidden!");

    }
    @RequestMapping(value="/{id}",method = RequestMethod.PUT)//,produces = "application/hal+json"
    public ResponseEntity updateComment(@PathVariable @Valid Long id, @RequestBody @Valid Comment comment, HttpServletRequest request) throws URISyntaxException {
        log.info("----- method put for comment -----");
        Validate.notEmpty(comment.getUsername());
        Comment commentOriginal = commentRepository.findOne(id);
        Validate.notNull(commentOriginal);
        if(checkAuthorizationOfAuthenticatedUser(commentOriginal.getUsername())){
            comment.setId(commentOriginal.getId());
            commentRepository.save(comment);
            HttpHeaders httpHeaders = new HttpHeaders();
            URI uri = new URI(request.getRequestURL().toString());
            httpHeaders.setLocation(uri);
            return new ResponseEntity(httpHeaders,HttpStatus.NO_CONTENT);
        }
        throw new AccessDeniedException("Access Forbidden!");
    }
    private boolean checkAuthorizationOfAuthenticatedUser(String userName){
        boolean userIsTheOwner = false;
        if(SecurityContextHolder.getContext().getAuthentication().getName().equals(userName)
                ){
            userIsTheOwner = true;
        }
        return userIsTheOwner;
    }
    @RequestMapping(value="/{id}",method = RequestMethod.DELETE)//,produces = "application/hal+json"
    public ResponseEntity deleteCommesnt(){
        return new ResponseEntity(HttpStatus.METHOD_NOT_ALLOWED);
    }

   @RequestMapping(value="/search/findCommentsByMilestone", params = {"milestone"},method = RequestMethod.GET)//produces = "application/hal+json"
   public ResponseEntity findCommentsByMilestone(@RequestParam(value = "milestone") @Valid Milestone milestone){

        log.info("Inside Find comments By Milestone");

       //Validate.notNull(milestone);
       // commentRepository.findByMilestoneOrderByDate(milestone,null);// Todo :check giving pageable null will return one page by default?????
       // List<Comment> comments = commentRepository.findByMilestoneOrderByDate(milestone,);

       // return new ResponseEntity(comments,HttpStatus.OK);
       return null;
    }
}