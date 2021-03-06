package io.github.mike10004.harreplay.exec;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import io.github.mike10004.harreplay.exec.HarReplayMain.HarReaderBehavior;
import io.github.mike10004.harreplay.tests.Fixtures;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class HarReplayMainTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void main0() throws Exception {
        assertEquals("exit code", 1, new NonSleepingHarReplayMain().main0(new String[]{}));
    }

    @Test
    public void readBrowsermobGeneratedFile() throws Exception {
        File harFile = getBrowsermobGeneratedHarFile();
        HarReplayMain main = new NonSleepingHarReplayMain();
        int exitCode = main.main0(new String[]{harFile.getAbsolutePath()});
        assertEquals("exit code", 0, exitCode);
    }

    @Test
    public void readBrowsermobGeneratedFile_failBecauseStockHarBehavior() throws Exception {
        File harFile = getBrowsermobGeneratedHarFile();
        HarReplayMain main = new NonSleepingHarReplayMain();
        try {
            main.main0(new String[]{
                    harFile.getAbsolutePath(),
                    "--har-reader-behavior", HarReaderBehavior.STOCK.name()
            });
            fail("exception should be thrown here");
        } catch (Exception e) {
            List<Throwable> causes = ImmutableList.copyOf(Throwables.getCausalChain(e));
            boolean hasInvalidFormatEx = causes.stream().anyMatch(com.fasterxml.jackson.databind.exc.InvalidFormatException.class::isInstance);
            assertTrue("expected exception not found in cause chain: " + causes, hasInvalidFormatEx);
        }
    }

    private File getBrowsermobGeneratedHarFile() throws IOException {
        return Fixtures.copyBrowsermobGeneratedHarFile(temporaryFolder.getRoot().toPath());
    }

    private static class NonSleepingHarReplayMain extends HarReplayMain {
        @Override
        protected void sleepForever() {
        }
    }

    @Test
    public void printVersion() throws UnsupportedEncodingException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(64);
        PrintStream ps = new PrintStream(baos, true);
        new HarReplayMain().printVersion(ps);
        ps.flush();
        String actual = baos.toString(UTF_8.name());
        System.out.print(actual);
        assertFalse("actual version string", actual.contains(HarReplayMain.DEFAULT_VERSION));
    }

    @Test
    public void loadMavenProperties() {
        Properties p = HarReplayMain.loadMavenProperties();
        p.stringPropertyNames().forEach(key -> {
            String value = p.getProperty(key);
            System.out.format("%s = %s%n", key, value);
            assertFalse("expect filtered: " + value, value.startsWith("${"));
        });
    }

}