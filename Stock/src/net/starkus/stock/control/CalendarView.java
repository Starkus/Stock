package net.starkus.stock.control;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;

public class CalendarView extends GridPane {
	
	
	private final ReadOnlyObjectWrapper<LocalDate> datePicked = new ReadOnlyObjectWrapper<>();
	private final ObjectProperty<LocalDate> monthShown = new SimpleObjectProperty<>();

	
	public CalendarView() {
		
		this.getStylesheets().add(CalendarView.class.getResource("CalendarView.css").toExternalForm());
		this.getStyleClass().add("calendar-view");
		
		LocalDate today = LocalDate.now();
		
		/* Any week does it really... */
		LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);
		LocalDate endOfWeek = startOfWeek.plusDays(6);
		
		
		/* Headers */
		DateTimeFormatter dayOfWeekFormatter = DateTimeFormatter.ofPattern("EEEEE");
		
		for (LocalDate date = startOfWeek; !date.isAfter(endOfWeek); date = date.plusDays(1)) {
			Label label = new Label(date.format(dayOfWeekFormatter));
			label.setPadding(new Insets(1));
			label.setTextAlignment(TextAlignment.CENTER);
			GridPane.setHalignment(label, HPos.CENTER);
			
			label.getStyleClass().add("day-header");
			
			this.add(label, date.getDayOfWeek().getValue(), 0);
		}
		
		
		monthShown.set(today);
		
		makeMonthTable(today);
		
		datePicked.set(today);
		
		
		monthShown.addListener((obs, oldMonth, newMonth) -> {
			makeMonthTable(newMonth);
		});
	}
	
	protected void makeMonthTable(LocalDate monthDate) {
		
		this.getChildren().clear();
		
		LocalDate startOfMonth = monthDate.minusDays(monthDate.getDayOfMonth() - 1);
		LocalDate endOfMonth = startOfMonth.plusDays(startOfMonth.getMonth().length(startOfMonth.isLeapYear()) - 1);
		
		LocalDate startOfCalendar = startOfMonth.minusDays(startOfMonth.getDayOfWeek().getValue() - 1);
		LocalDate endOfCalendar = endOfMonth.plusDays(7 - endOfMonth.getDayOfWeek().getValue());
		
		// Iterate through month days //
		int rowIndex = 1;
		for (LocalDate date = startOfCalendar; !date.isAfter(endOfCalendar); date = date.plusDays(1)) {
			
			DayCell cell = new DayCell(date);
			
			datePicked.addListener((obs, previousDate, newDate) -> {
				
				if (cell.getDate().equals(newDate))
					cell.showMark();
					
				else if (cell.getDate().equals(previousDate))
					cell.hideMark();
			});
			
			if (cell.getDate().equals(datePicked.get()))
				cell.showMark();
			
			cell.setOnAction(e -> {
				if (datePicked.get() != cell.getDate())
					datePicked.set(cell.getDate());
				
				else
					datePicked.set(null);
			});
			
			if (date.isBefore(startOfMonth) || date.isAfter(endOfMonth)) {
				cell.getStyleClass().add("day-cell-offmonth");
			}
			
			
			this.add(cell, date.getDayOfWeek().getValue(), rowIndex);
			
			/* Continue below */
			if (date.getDayOfWeek() == DayOfWeek.SUNDAY)
				rowIndex++;
		}
	}
	
	public LocalDate getDatePicked() {
		return datePicked.get();
	}
	
	public ReadOnlyObjectProperty<LocalDate> datePickedProperty() {
		return datePicked.getReadOnlyProperty();
	}

	
	public LocalDate getMonthShown() {
		return monthShown.get();
	}
	
	public void setMonthShown(LocalDate date) {
		
		LocalDate startOfMonth = date.minusDays(date.getDayOfMonth() - 1);
		
		monthShown.set(startOfMonth);
	}
	public ObjectProperty<LocalDate> monthShownProperty() {
		return monthShown;
	}
}
