package com.insane.eyewalk.api.controller.rest;

import com.insane.eyewalk.api.config.ModelMapperList;
import com.insane.eyewalk.api.model.domain.Contact;
import com.insane.eyewalk.api.model.domain.User;
import com.insane.eyewalk.api.model.input.ContactInput;
import com.insane.eyewalk.api.model.view.ContactView;
import com.insane.eyewalk.api.repositories.ContactRepository;
import com.insane.eyewalk.api.repositories.UserRepository;
import com.insane.eyewalk.api.service.ContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final ContactRepository contactRepository;
    private final UserRepository userRepository;
    private final ContactService contactService;

    @Operation(
            summary = "List contacts",
            description = "Get the contact list from user. User must be logged in.",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Unauthorized", responseCode = "403"),
                    @ApiResponse(description = "Not Found | User not found", responseCode = "404"),
                    @ApiResponse(description = "Invalid Token | Expired Token", responseCode = "500")
            }
    )
    @GetMapping
    @ResponseBody
    public ResponseEntity<List<ContactView>> listAll(Principal principal) {
        if (principal == null)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        try {
            return ResponseEntity.ok(modelMapping.mapList(
                                         contactRepository.findAllByUser(
                                         userRepository.findByEmail(principal.getName()).orElseThrow()
                                         ), ContactView.class)
                                    );
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @Operation(
            summary = "Get contact",
            description = "Get a contact from user. User must be logged in.",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Unauthorized", responseCode = "403"),
                    @ApiResponse(description = "Not Found | User not found", responseCode = "404"),
                    @ApiResponse(description = "Invalid Token | Expired Token", responseCode = "500")
            }
    )
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<ContactView> getContact(@PathVariable long id, Principal principal) {
        if (principal == null)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        try {
            User user = userRepository.findByEmail(principal.getName()).orElseThrow();
            Contact contact = contactRepository.findByIdAndUser(id, user);
            if (contact != null)
                return ResponseEntity.ok(modelMapping.map(contact, ContactView.class));
        } catch (NoSuchElementException ignoredException) {}
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @Operation(
            summary = "Save contact",
            description = "Save a contact. User must be logged in.",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Unauthorized", responseCode = "403"),
                    @ApiResponse(description = "Not Found | User not found", responseCode = "404"),
                    @ApiResponse(description = "Invalid Token | Expired Token", responseCode = "500")
            }
    )
    @PostMapping
    @ResponseBody
    public ResponseEntity<ContactView> addContact(@RequestBody ContactInput contactInput, Principal principal) {
        if (principal != null) {
            try {
                User user = userRepository.findByEmail(principal.getName()).orElseThrow();
                return ResponseEntity.ok(modelMapping.map(
                                                contactService.registerContact(user, contactInput),
                                                ContactView.class));
            } catch (NoSuchElementException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
    }

    @Operation(
            summary = "Delete contact",
            description = "Delete a contact. User must be logged in.",
            responses = {
                    @ApiResponse(description = "Successfully Deleted", responseCode = "200"),
                    @ApiResponse(description = "Unauthorized", responseCode = "403"),
                    @ApiResponse(description = "Not Found | Contact not found", responseCode = "404"),
                    @ApiResponse(description = "Invalid Token | Expired Token", responseCode = "500")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable long id, Principal principal) {
        if (principal != null) {
            try {
                User user = userRepository.findByEmail(principal.getName()).orElseThrow();
                Contact contact = contactRepository.findByIdAndUser(id, user);
                if (contact == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(HttpStatus.NOT_FOUND);
                contactRepository.delete(contact);
                return ResponseEntity.ok(HttpStatus.OK);
            } catch (NoSuchElementException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(HttpStatus.NOT_FOUND);
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(HttpStatus.FORBIDDEN);
    }

}
