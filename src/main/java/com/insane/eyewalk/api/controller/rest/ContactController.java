package com.insane.eyewalk.api.controller.rest;

import com.insane.eyewalk.api.config.ModelMapperList;
import com.insane.eyewalk.api.model.domain.Contact;
import com.insane.eyewalk.api.model.domain.User;
import com.insane.eyewalk.api.model.input.ContactInput;
import com.insane.eyewalk.api.model.input.ContactPictureInput;
import com.insane.eyewalk.api.model.view.ContactView;
import com.insane.eyewalk.api.service.ContactService;
import com.insane.eyewalk.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.InvalidFileNameException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/contact")
@RequiredArgsConstructor
@Tag(name = "Contact")
public class ContactController {

    private final ModelMapperList modelMapping;
    private final ContactService contactService;
    private final UserService userService;

    @Operation(
            summary = "List contacts",
            description = "Get the contact list from user. User must be authenticated.",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Unauthorized | User not found", responseCode = "401"),
                    @ApiResponse(description = "Forbidden | Invalid token", responseCode = "403"),
                    @ApiResponse(description = "Invalid Token | Expired Token", responseCode = "500")
            }
    )
    @GetMapping
    @ResponseBody
    public ResponseEntity<List<ContactView>> getContactList(Principal principal) {
        if (principal != null) {
            try {
                User user = userService.getUser(principal.getName());
                return ResponseEntity.ok(modelMapping.mapList(contactService.getContactList(user), ContactView.class));
            } catch (UsernameNotFoundException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
    }

    @Operation(
            summary = "Get contact",
            description = "Get a contact from user. User must be authenticated.",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Unauthorized | User not found", responseCode = "401"),
                    @ApiResponse(description = "Forbidden | Invalid token", responseCode = "403"),
                    @ApiResponse(description = "Not Found | Contact not found", responseCode = "404"),
                    @ApiResponse(description = "Invalid Token | Expired Token", responseCode = "500")
            }
    )
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<ContactView> getContact(@PathVariable long id, Principal principal) {
        if (principal != null) {
            try {
                User user = userService.getUser(principal.getName());
                Contact contact = contactService.getContact(id, user);
                return ResponseEntity.ok(modelMapping.map(contact, ContactView.class));
            } catch (UsernameNotFoundException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            } catch (NoSuchElementException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
    }

    @Operation(
            summary = "Save contact",
            description = "Save a contact. User must be authenticated.",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Unauthorized | User not found", responseCode = "401"),
                    @ApiResponse(description = "Forbidden | Invald token", responseCode = "403"),
                    @ApiResponse(description = "Invalid Token | Expired Token", responseCode = "500")
            }
    )
    @PostMapping
    @ResponseBody
    public ResponseEntity<ContactView> createContact(@RequestBody ContactInput contactInput, Principal principal) {
        if (principal != null) {
            try {
                User user = userService.getUser(principal.getName());
                Contact contact = contactService.createContact(user, contactInput);
                return ResponseEntity.ok(modelMapping.map(contact, ContactView.class));
            } catch (UsernameNotFoundException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
    }

    @Operation(
            summary = "Add pictures to a contact",
            description = "This post must be sent as form-data and not as Json body! With the authorization bearer on header and the contact id on the form together with the files. The pictures total size must not exceed 2Mb. Only .jpg and .png files are accepted. User must be authenticated.",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Unauthorized | User not found", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403"),
                    @ApiResponse(description = "Not Acceptable | Not png or jpg file", responseCode = "406"),
                    @ApiResponse(description = "Not Found | Contact not found", responseCode = "404"),
                    @ApiResponse(description = "Max size allowed exceed | Expired Token", responseCode = "500")
            }
    )
    @PostMapping("/pictures")
    @ResponseBody
    public ResponseEntity<ContactView> addPicturesToContact(@ModelAttribute ContactPictureInput contactPictureInput, Principal principal) {
        if (principal != null) {
            try {
                User user = userService.getUser(principal.getName());
                Contact contact = contactService.addPictures(user, contactPictureInput);
                return ResponseEntity.ok(modelMapping.map(contact, ContactView.class));
            } catch (UsernameNotFoundException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            } catch (NoSuchElementException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            } catch (InvalidFileNameException e) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
            }
        } return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
    }

    @Operation(
            summary = "Delete contact",
            description = "Delete a contact. User must be authenticated.",
            responses = {
                    @ApiResponse(description = "Successfully Deleted", responseCode = "200"),
                    @ApiResponse(description = "Unauthorized | User not found", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403"),
                    @ApiResponse(description = "Not Found | Contact not found", responseCode = "404"),
                    @ApiResponse(description = "Invalid Token | Expired Token", responseCode = "500")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteContact(@PathVariable long id, Principal principal) {
        if (principal != null) {
            try {
                User user = userService.getUser(principal.getName());
                contactService.deleteContact(id, user);
                return ResponseEntity.ok(HttpStatus.OK);
            } catch (UsernameNotFoundException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            } catch (NoSuchElementException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(HttpStatus.NOT_FOUND);
            }
        } return ResponseEntity.status(HttpStatus.FORBIDDEN).body(HttpStatus.FORBIDDEN);
    }

}
