package com.koios.journalapp.controller;

import com.koios.journalapp.model.JournalEntry;
import com.koios.journalapp.model.User;
import com.koios.journalapp.service.JournalEntryService;
import com.koios.journalapp.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/journal")
public class JournalEntryController {

    private final JournalEntryService journalEntryService;
    private final UserService userService;

    public JournalEntryController(JournalEntryService journalEntryService, UserService userService) {
        this.journalEntryService = journalEntryService;
        this.userService = userService;
    }

    @PostMapping("/{username}")
    public ResponseEntity<?> createEntry(@RequestBody JournalEntry journalEntry, @PathVariable String username){
        try {
            journalEntryService.saveEntry(journalEntry, username);
            return new ResponseEntity<>(journalEntry, HttpStatus.CREATED);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getAllJournalOfUser(@PathVariable String username){
        User user = userService.findByUsername(username);
        if(user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        List<JournalEntry> journalEntry = user.getJournalEntries();
        if(journalEntry.isEmpty()) {
            return new ResponseEntity<>("Journal is empty",HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(journalEntry, HttpStatus.OK);
    }

    @DeleteMapping("/{username}/{id}")
    public ResponseEntity<?> deleteByID(@PathVariable long id, @PathVariable String username) {
        if(userService.findByUsername(username) == null)
            return new ResponseEntity<>("User not Found", HttpStatus.NOT_FOUND);
        Optional<JournalEntry> journalEntry = journalEntryService.findById(id);
        if(journalEntry.isPresent()) {
            journalEntryService.deleteById(id, username);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>("Journal not found",HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{username}/{id}")
    public ResponseEntity<?> updateJournalById(@RequestBody JournalEntry myEntry,
                                               @PathVariable long id,
                                               @PathVariable String username) {
        JournalEntry oldEntry = journalEntryService.findById(id).orElse(null);
        if(oldEntry != null) {
            oldEntry.setTitle(myEntry.getTitle() == null && myEntry.getTitle().equals("")
                    ? oldEntry.getTitle(): myEntry.getTitle());
            oldEntry.setContent(myEntry.getContent() == null && myEntry.getContent().equals("")
                    ? oldEntry.getContent(): myEntry.getContent());
            journalEntryService.saveEntry(oldEntry);
            return new ResponseEntity<>(oldEntry, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
