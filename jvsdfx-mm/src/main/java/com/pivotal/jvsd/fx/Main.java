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
package com.pivotal.jvsd.fx;

import com.pivotal.jvsd.controller.RootController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * @author Jens Deppe
 */
public class Main extends Application {

  private Stage primaryStage;

  private RootController controller;

  @Override
  public void start(Stage stage) throws IOException {
    primaryStage = stage;
    primaryStage.setTitle("jVSD");

    FXMLLoader loader = new FXMLLoader(getClass().getResource("/jvsd.fxml"));
    Pane rootLayout = loader.load();

    Scene scene = new Scene(rootLayout);
    scene.getStylesheets().add("/jvsd.css");

    controller = loader.getController();

    ChartManager.getInstance().setRootController(controller);

    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public static void main(String[] args) throws IOException {
    StatFileManager.getInstance().add(args);
    launch(args);
  } 
}

