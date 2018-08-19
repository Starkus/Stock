package net.starkus.stock.control;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.TextAlignment;

public class DayCell extends Button {
	
	private static final DateTimeFormatter dayOfMonthFormatter = DateTimeFormatter.ofPattern("d");
	
	private final Region mark;
	
	private LocalDate date;
	
	public DayCell(LocalDate date) {
		
		this.date = date;
		setText(date.format(dayOfMonthFormatter));
		
		setPadding(new Insets(1));
		setTextAlignment(TextAlignment.CENTER);
		GridPane.setHalignment(this, HPos.CENTER);
		GridPane.setHgrow(this, Priority.SOMETIMES);
		GridPane.setVgrow(this, Priority.SOMETIMES);
		
		getStyleClass().add("day-cell");

		mark = new Region();
		mark.setMinSize(24, 24);
		mark.setStyle("-fx-background-color: transparent;");
		setGraphic(mark);
	}

	public LocalDate getDate() {
		return date;
	}
	
	public void showMark() {
		mark.setStyle(
				  "-fx-background-color: #e43f6e;"
				+ "-fx-background-radius: 12px;");
	}
	
	public void hideMark() {
		mark.setStyle("-fx-background-color: transparent;");
	}
}
