package org.jabref.logic.util.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.jabref.gui.DialogService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class FileNameUniquenessTest {

    @TempDir
    protected Path tempDir;

    @Test
    void getNonOverWritingFileNameReturnsSameName() throws IOException {
        assertFalse(Files.exists(tempDir.resolve("sameFile.txt")));

        String outputFileName = FileNameUniqueness.getNonOverWritingFileName(tempDir, "sameFile.txt");
        assertEquals("sameFile.txt", outputFileName);
    }

    @Test
    void getNonOverWritingFileNameReturnsUniqueNameOver1Conflict() throws IOException {
        Path dummyFilePath1 = tempDir.resolve("differentFile.txt");

        Files.createFile(dummyFilePath1);

        String outputFileName = FileNameUniqueness.getNonOverWritingFileName(tempDir, "differentFile.txt");
        assertEquals("differentFile (1).txt", outputFileName);
    }

    @Test
    void getNonOverWritingFileNameReturnsUniqueNameOverNConflicts() throws IOException {
        Path dummyFilePath1 = tempDir.resolve("manyfiles.txt");
        Path dummyFilePath2 = tempDir.resolve("manyfiles (1).txt");

        Files.createFile(dummyFilePath1);
        Files.createFile(dummyFilePath2);

        String outputFileName = FileNameUniqueness.getNonOverWritingFileName(tempDir, "manyfiles.txt");
        assertEquals("manyfiles (2).txt", outputFileName);
    }

    @Test
    void isDuplicatedFileWithNoSimilarNames() throws IOException {
        DialogService dialogService = mock(DialogService.class);
        String filename1 = "file1.txt";
        Path filePath1 = tempDir.resolve(filename1);
        Files.createFile(filePath1);

        boolean isDuplicate = FileNameUniqueness.isDuplicatedFile(tempDir, filePath1, dialogService);
        assertFalse(isDuplicate);
    }

    @Test
    void isDuplicatedFileWithOneSimilarNames() throws IOException {
        DialogService dialogService = mock(DialogService.class);
        String filename1 = "file.txt";
        String filename2 = "file (1).txt";
        Path filePath1 = tempDir.resolve(filename1);
        Path filePath2 = tempDir.resolve(filename2);
        Files.createFile(filePath1);
        Files.createFile(filePath2);

        boolean isDuplicate = FileNameUniqueness.isDuplicatedFile(tempDir, filePath2, dialogService);
        assertTrue(isDuplicate);
    }

    @Test
    void taseDuplicateMarksReturnsOrignalFileName1() throws IOException {
        String fileName1 = "abc def (1)";
        String fileName2 = FileNameUniqueness.eraseDuplicateMarks(fileName1);
        assertEquals("abc def", fileName2);
    }

    @Test
    void taseDuplicateMarksReturnsOrignalFileName2() throws IOException {
        String fileName1 = "abc (def) gh (1)";
        String fileName2 = FileNameUniqueness.eraseDuplicateMarks(fileName1);
        assertEquals("abc (def) gh", fileName2);
    }

    @Test
    void taseDuplicateMarksReturnsSameName1() throws IOException {
        String fileName1 = "abc def (g)";
        String fileName2 = FileNameUniqueness.eraseDuplicateMarks(fileName1);
        assertEquals("abc def (g)", fileName2);
    }

    @Test
    void taseDuplicateMarksReturnsSameName2() throws IOException {
        String fileName1 = "abc def";
        String fileName2 = FileNameUniqueness.eraseDuplicateMarks(fileName1);
        assertEquals("abc def", fileName2);
    }
}
