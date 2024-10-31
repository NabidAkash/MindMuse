package com.koios.journalapp.service;

import com.koios.journalapp.model.JournalEntry;
import com.koios.journalapp.model.User;
import com.koios.journalapp.repository.JournalEntryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class JournalEntryService {
    private final JournalEntryRepository journalEntryRepository;
    private final UserService userService;

    public JournalEntryService(JournalEntryRepository journalEntryRepository, UserService userService) {
        this.journalEntryRepository = journalEntryRepository;
        this.userService = userService;
    }
    @Transactional
    public void saveEntry(JournalEntry journalEntry, String username) {
        journalEntry.setDate(LocalDateTime.now());
        journalEntryRepository.save(journalEntry);
        User user = userService.findByUsername(username);
        user.getJournalEntries().add(journalEntry);
        user.setUsername(null);
        userService.saveUser(user);
    }

    public void saveEntry(JournalEntry journalEntry) {
        journalEntryRepository.save(journalEntry);
    }

    public List<JournalEntry> getAll() {
        return journalEntryRepository.findAll();
    }

    public Optional<JournalEntry> findById(long id){
        return journalEntryRepository.findById(id);
    }

    @Transactional
    public void deleteById(long id, String username){
        User user = userService.findByUsername(username);
        user.getJournalEntries().removeIf(i -> i.getId().equals(id));
        userService.saveUser(user);
        journalEntryRepository.deleteById(id);
    }
}
