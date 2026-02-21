package com.library.services;

import com.library.constants.ErrorMessages;
import com.library.exception.DuplicateRecordException;
import com.library.exception.InvalidEmailException;
import com.library.exception.InvalidPatronException;
import com.library.exception.PatronNotFoundException;
import com.library.models.Patron;
import com.library.repositories.PatronRepository;
import com.library.validator.EmailValidator;
import com.library.validator.PhoneValidator;
import java.util.List;

public class PatronService {
    private final PatronRepository patronRepository;
    private final EmailValidator emailValidator = new EmailValidator();
    private final PhoneValidator phoneValidator = new PhoneValidator();

    public PatronService(PatronRepository patronRepository) {
        this.patronRepository = patronRepository;
    }

    public void registerPatron(Patron patron) {
        validate(patron);
        if (patronRepository.exists(patron.getPatronId())) {
            throw new DuplicateRecordException(ErrorMessages.DUPLICATE_RECORD);
        }
        patronRepository.save(patron);
    }

    public Patron getPatron(String patronId) {
        Patron patron = patronRepository.findById(patronId);
        if (patron == null) {
            throw new PatronNotFoundException(ErrorMessages.PATRON_NOT_FOUND);
        }
        return patron;
    }

    public void updatePatron(Patron updated) {
        validate(updated);
        Patron patron = getPatron(updated.getPatronId());
        patron.setName(updated.getName());
        patron.setEmail(updated.getEmail());
        patron.setPhone(updated.getPhone());
        patron.setActive(updated.isActive());
    }

    public List<Patron> listPatrons() {
        return patronRepository.findAll();
    }

    private void validate(Patron patron) {
        if (patron.getName() == null || patron.getName().trim().isEmpty()) {
            throw new InvalidPatronException(ErrorMessages.INVALID_PATRON);
        }
        if (!emailValidator.isValid(patron.getEmail())) {
            throw new InvalidEmailException(ErrorMessages.INVALID_EMAIL);
        }
        if (!phoneValidator.isValid(patron.getPhone())) {
            throw new InvalidPatronException(ErrorMessages.INVALID_PATRON);
        }
    }
}
