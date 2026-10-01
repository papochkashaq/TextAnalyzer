package org.example.textanalyzer;

import org.example.textanalyzer.config.MultithreadingMode;
import org.example.textanalyzer.io.FileCollector;
import org.example.textanalyzer.model.AnalysisInfo;
import org.example.textanalyzer.model.CountTopWordsResult;
import org.example.textanalyzer.model.WordCount;
import org.example.textanalyzer.output.Printer;
import org.example.textanalyzer.output.ResultOutput;
import org.example.textanalyzer.parser.FileParser;
import org.example.textanalyzer.service.WordCounter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TextAnalyzerRunnerTest {

    @Mock
    private FileParser fileParser;
    @Mock
    private FileCollector fileCollector;
    @Mock
    private WordCounter wordCounter;
    @Mock
    private Printer printer;
    @InjectMocks
    private TextAnalyzerRunner runner;

    @Test
    void shouldExecuteSuccessfullyWhenAllArgumentsProvided() throws Exception {
        setField("dirPath", Path.of("./texts"));
        setField("minLength", 5);
        setField("top", 10);
        setField("mode", MultithreadingMode.MULTI);
        setField("threads", 4);
        setField("outputPath", null);
        setField("stopWordsPath", Path.of("./stop.txt"));

        List<Path> files = List.of(Path.of("file1.txt"), Path.of("file2.txt"));
        when(fileCollector.collectFiles(Path.of("./texts")))
                .thenReturn(files);

        when(fileParser.parseStopWords(Path.of("./stop.txt")))
                .thenReturn(Set.of("the", "a"));

        when(wordCounter.countTopWords(eq(files), eq(10), any(), eq(4), eq(MultithreadingMode.MULTI)))
                .thenReturn(new CountTopWordsResult(List.of(new WordCount("java", 5)), List.of()));

        Integer exitCode = runner.call();

        assertThat(exitCode).isZero();
        verify(fileCollector).collectFiles(Path.of("./texts"));
        verify(fileParser).parseStopWords(Path.of("./stop.txt"));
        verify(wordCounter).countTopWords(eq(files), eq(10), any(), eq(4), eq(MultithreadingMode.MULTI));
        verify(printer).print(any(ResultOutput.class), isNull());
    }

    @Test
    void shouldHandleOutputPath() throws Exception {
        setField("dirPath", Path.of("./texts"));
        setField("minLength", 5);
        setField("top", 10);
        setField("mode", MultithreadingMode.MULTI);
        setField("threads", 4);
        setField("outputPath", Path.of("./output.json"));
        setField("stopWordsPath", null);

        when(fileCollector.collectFiles(any())).thenReturn(List.of(Path.of("file.txt")));
        when(wordCounter.countTopWords(any(), anyInt(), any(), eq(4), eq(MultithreadingMode.MULTI)))
                .thenReturn(new CountTopWordsResult(List.of(), List.of()));

        Integer exitCode = runner.call();

        assertThat(exitCode).isZero();
        verify(printer).print(any(ResultOutput.class), eq(Path.of("./output.json")));
    }

    @Test
    void shouldReturnExitCode1WhenDirectoryInvalid() throws Exception {
        setField("dirPath", Path.of("./nonexistent"));
        setField("minLength", 5);
        setField("top", 10);
        setField("mode", MultithreadingMode.MULTI);
        setField("threads", 4);
        setField("outputPath", null);
        setField("stopWordsPath", null);

        when(fileCollector.collectFiles(any()))
                .thenThrow(new IllegalArgumentException("Invalid directory"));

        Integer exitCode = runner.call();

        assertThat(exitCode).isEqualTo(1);
        verify(printer, never()).print(any(), any());
    }

    @Test
    void shouldReturnExitCode1WhenParserFails() throws Exception {
        setField("dirPath", Path.of("./texts"));
        setField("minLength", 5);
        setField("top", 10);
        setField("mode", MultithreadingMode.MULTI);
        setField("threads", 4);
        setField("outputPath", null);
        setField("stopWordsPath", Path.of("./stopwords.txt"));

        lenient().when(fileParser.parseStopWords(any())).thenThrow(new IOException("Cannot read"));

        Integer exitCode = runner.call();

        assertThat(exitCode).isEqualTo(1);
        verify(fileCollector, never()).collectFiles(any());
        verify(wordCounter, never()).countTopWords(any(), anyInt(), any(), anyInt(), any());
        verify(printer, never()).print(any(), any());
    }

    @Test
    void shouldHandleEmptyFileList() throws Exception {
        setField("dirPath", Path.of("./empty"));
        setField("minLength", 5);
        setField("top", 10);
        setField("mode", MultithreadingMode.MULTI);
        setField("threads", 4);
        setField("outputPath", null);
        setField("stopWordsPath", null);

        when(fileCollector.collectFiles(any())).thenReturn(List.of());
        when(wordCounter.countTopWords(any(), anyInt(), any(), anyInt(), any()))
                .thenReturn(new CountTopWordsResult(List.of(), List.of()));

        Integer exitCode = runner.call();

        assertThat(exitCode).isZero();
        verify(wordCounter).countTopWords(eq(List.of()), anyInt(), any(), anyInt(), any());
    }

    @Test
    void shouldPassModeAndThreadsAndBuildCorrectAnalysisInfo() throws Exception {
        setField("dirPath", Path.of("./texts"));
        setField("minLength", 5);
        setField("top", 10);
        setField("outputPath", null);
        setField("stopWordsPath", null);
        setField("threads", 4);
        setField("mode", MultithreadingMode.MULTI);

        List<Path> files = List.of(Path.of("file1.txt"), Path.of("file2.txt"));
        when(fileCollector.collectFiles(any())).thenReturn(files);
        when(wordCounter.countTopWords(eq(files), eq(10), any(), eq(4), eq(MultithreadingMode.MULTI)))
                .thenReturn(new CountTopWordsResult(List.of(new WordCount("java", 5)), List.of()));

        Integer exitCode = runner.call();

        ArgumentCaptor<ResultOutput> captor = ArgumentCaptor.forClass(ResultOutput.class);
        verify(printer).print(captor.capture(), isNull());

        AnalysisInfo info = captor.getValue().analysisInfo();
        assertThat(exitCode).isZero();
        assertThat(info.mode()).isEqualTo(MultithreadingMode.MULTI);
        assertThat(info.threads()).isEqualTo(4);
        assertThat(info.processedFiles()).isEqualTo(files.size());
    }


    private void setField(String fieldName, Object value) throws Exception {
        Field field = TextAnalyzerRunner.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(runner, value);
    }

}




