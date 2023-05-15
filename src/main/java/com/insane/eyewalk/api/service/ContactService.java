package com.insane.eyewalk.api.service;

import com.insane.eyewalk.api.config.ModelMapperList;
import com.insane.eyewalk.api.model.domain.Contact;
import com.insane.eyewalk.api.model.domain.User;
import com.insane.eyewalk.api.model.input.ContactInput;
import com.insane.eyewalk.api.model.input.ContactPictureInput;
import com.insane.eyewalk.api.repositories.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.InvalidFileNameException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ModelMapperList modelMapping;
    private final ContactRepository contactRepository;
    private final EmailService emailService;
    private final PhoneService phoneService;
    private final PictureService pictureService;

    /**
     * Method to list all contacts from a specific user
     * @param user the authenticated user
     * @return a List of contact if none it will return an empty list
     */
    public List<Contact> getContactList(User user) {
        return contactRepository.findAllByUser(user);
    }

    /**
     * Method to retrieve a contact details from a specific user's contact
     * @param user the authenticated user
     * @param contactId the contact id
     * @return a Contact object or throws an exception if no contact was found
     * @throws NoSuchElementException if no contact was found
     */
    public Contact getContact(long contactId, User user) throws NoSuchElementException {
        Contact contact = contactRepository.findByIdAndUser(contactId, user);
        if (contact == null) throw new NoSuchElementException("No contact found with id number "+contactId);
        return contact;
    }

    /**
     * Method to delete a contact from user's contact list
     * @param id contact id
     * @param user the authenticated user
     * @throws NoSuchElementException if no contact was found
     */
    public void deleteContact(long id, User user) throws NoSuchElementException {
        contactRepository.delete(getContact(id, user));
    }

    /**
     * Method to register a new User Contact
     * @param user the logged user requesting to add this contact
     * @param contactInput contact input body required
     * @return Saved Contact
     */
    public Contact createContact(User user, ContactInput contactInput) {
        Contact contact = modelMapping.map(contactInput, Contact.class);
        contact.setUser(user);
        contact.setEmails(emailService.saveEmails(contactInput.getEmails()));
        contact.setPhones(phoneService.savePhones(contactInput.getPhones()));
        return contactRepository.save(contact);
    }

    /**
     * Method to add pictures to a user contact
     * @param user the logged user requesting to add this pictures
     * @param contactPictureInput picture input body required
     * @return Saved Contact
     * @throws NoSuchElementException if no contact is found on user's list with the contact id provided
     * @throws InvalidFileNameException if the file is not type jpg or png
     */
    public Contact addPictures(User user, ContactPictureInput contactPictureInput) throws NoSuchElementException, InvalidFileNameException {
        Contact contact = contactRepository.findByIdAndUser(contactPictureInput.getContactId(), user);
        if (contact != null) {
            contact.getPictures().addAll((pictureService.savePictures(contactPictureInput.getFiles())));
            return contactRepository.save(contact);
        }
        throw new NoSuchElementException("Contact not found on user's list");
    }

    /**
     * Method to verify if a picture filename exists in a user's contact
     * @param filename the picture file name
     * @param contact the contact owning the picture
     * @return boolean true if the picture exists in the contact pictures list.
     */
    public boolean existPicture(String filename, Contact contact) {
        return contact.getPictures().stream().anyMatch(picture -> picture.getFilename().equalsIgnoreCase(filename));
    }

}
