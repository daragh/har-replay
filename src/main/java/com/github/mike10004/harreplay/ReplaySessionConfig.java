package com.github.mike10004.harreplay;

import com.google.common.collect.ImmutableList;
import org.apache.commons.io.input.TailerListener;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class ReplaySessionConfig {

    public final Path scratchDir;
    public final int port;
    public final File harFile;
    public final ServerReplayConfig serverReplayConfig;
    public final ImmutableList<TailerListener> stdoutListeners;
    public final ImmutableList<TailerListener> stderrListeners;

    private ReplaySessionConfig(Builder builder) {
        scratchDir = builder.scratchDir;
        port = builder.port;
        harFile = builder.harFile;
        serverReplayConfig = builder.serverReplayConfig;
        stdoutListeners = ImmutableList.copyOf(builder.stdoutListeners);
        stderrListeners = ImmutableList.copyOf(builder.stderrListeners);
    }

    public static Builder builder(Path scratchDir) {
        return new Builder(scratchDir);
    }

    public static final int DEFAULT_PORT = 49877;

    @SuppressWarnings("unused")
    public static final class Builder {

        private final Path scratchDir;
        private int port = DEFAULT_PORT;
        private File harFile;
        private ServerReplayConfig serverReplayConfig = ServerReplayConfig.basic();
        private List<TailerListener> stdoutListeners = new ArrayList<>();
        private List<TailerListener> stderrListeners = new ArrayList<>();

        private Builder(Path scratchDir) {
            this.scratchDir = checkNotNull(scratchDir);
        }

        public Builder port(int val) {
            checkArgument(port > 0 && port < 65536);
            port = val;
            return this;
        }

        public Builder config(ServerReplayConfig serverReplayConfig) {
            this.serverReplayConfig = serverReplayConfig;
            return this;
        }

        public Builder addStdoutListener(TailerListener val) {
            stdoutListeners.add(val);
            return this;
        }

        public Builder addStderrListener(TailerListener val) {
            stderrListeners.add(val);
            return this;
        }

        public ReplaySessionConfig build(File harFile) {
            this.harFile = checkNotNull(harFile);
            return new ReplaySessionConfig(this);
        }
    }
}
