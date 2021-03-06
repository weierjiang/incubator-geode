/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gemstone.gemfire.distributed;

import static com.gemstone.gemfire.internal.logging.log4j.custom.CustomConfiguration.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.experimental.categories.Category;

import com.gemstone.gemfire.internal.process.ProcessStreamReader;
import com.gemstone.gemfire.internal.process.ProcessType;
import com.gemstone.gemfire.internal.process.ProcessUtils;
import com.gemstone.gemfire.test.junit.categories.IntegrationTest;

/**
 * Integration tests for launching a Server in a forked process with custom logging configuration
 */
@Category(IntegrationTest.class)
public class ServerLauncherRemoteWithCustomLoggingIntegrationTest extends AbstractServerLauncherRemoteIntegrationTestCase {

  private File customConfigFile;

  @Rule
  public SystemOutRule systemOutRule = new SystemOutRule().enableLog();

  @Before
  public void setUpLocatorLauncherRemoteWithCustomLoggingIntegrationTest() throws Exception {
    this.customConfigFile = createConfigFileIn(this.temporaryFolder.getRoot());
  }

  @Test
  public void testStartUsesCustomLoggingConfiguration() throws Throwable {
    // build and start the server
    final List<String> jvmArguments = getJvmArguments();

    final List<String> command = new ArrayList<String>();
    command.add(new File(new File(System.getProperty("java.home"), "bin"), "java").getCanonicalPath());
    for (String jvmArgument : jvmArguments) {
      command.add(jvmArgument);
    }
    command.add("-D" + ConfigurationFactory.CONFIGURATION_FILE_PROPERTY + "=" + this.customConfigFile.getCanonicalPath());
    command.add("-cp");
    command.add(System.getProperty("java.class.path"));
    command.add(ServerLauncher.class.getName());
    command.add(ServerLauncher.Command.START.getName());
    command.add(getUniqueName());
    command.add("--disable-default-server");
    command.add("--redirect-output");

    this.process = new ProcessBuilder(command).directory(new File(this.workingDirectory)).start();
    this.processOutReader = new ProcessStreamReader.Builder(this.process).inputStream(this.process.getInputStream()).inputListener(new ToSystemOut()).build().start();
    this.processErrReader = new ProcessStreamReader.Builder(this.process).inputStream(this.process.getErrorStream()).inputListener(new ToSystemOut()).build().start();

    int pid = 0;
    this.launcher = new ServerLauncher.Builder()
            .setWorkingDirectory(this.temporaryFolder.getRoot().getCanonicalPath())
            .build();
    try {
      waitForServerToStart();

      // validate the pid file and its contents
      this.pidFile = new File(this.temporaryFolder.getRoot(), ProcessType.SERVER.getPidFileName());
      assertTrue(this.pidFile.exists());
      pid = readPid(this.pidFile);
      assertTrue(pid > 0);
      assertTrue(ProcessUtils.isProcessAlive(pid));

      final String logFileName = getUniqueName()+".log";
      assertTrue("Log file should exist: " + logFileName, new File(this.temporaryFolder.getRoot(), logFileName).exists());

      // check the status
      final ServerLauncher.ServerState serverState = this.launcher.status();
      assertNotNull(serverState);
      assertEquals(AbstractLauncher.Status.ONLINE, serverState.getStatus());

      assertThat(systemOutRule.getLog()).contains("log4j.configurationFile = " + this.customConfigFile.getCanonicalPath());
      assertThat(systemOutRule.getLog()).contains(CONFIG_LAYOUT_PREFIX);

    } catch (Throwable e) {
      this.errorCollector.addError(e);
    }

    // stop the server
    try {
      assertEquals(AbstractLauncher.Status.STOPPED, this.launcher.stop().getStatus());
      waitForPidToStop(pid);
    } catch (Throwable e) {
      this.errorCollector.addError(e);
    }
  }

  private static class ToSystemOut implements ProcessStreamReader.InputListener {
    @Override
    public void notifyInputLine(String line) {
      System.out.println(line);
    }
  }
}
