package com.koios.journalapp.repository;

import com.koios.journalapp.model.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {
 
}
