package com.safra.fragments;

import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.PLANNER_AIM_SPINNER_LIST;
import static com.safra.utilities.Common.PLANNER_GOAL_SPINNER_LIST;
import static com.safra.utilities.Common.PLANNER_PROJECT_SPINNER_LIST;
import static com.safra.utilities.Common.PLANNER_SCHEDULE_CALENDAR_LIST;
import static com.safra.utilities.UserSessionManager.userSessionManager;

import android.app.AlertDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.safra.R;
import com.safra.Safra;
import com.safra.databinding.FragmentSchedulesBinding;
import com.safra.models.ProjectSpinnerModel;
import com.safra.models.ScheduleCalendarModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class SchedulesFragment extends Fragment {
    public static final String TAG = "schedule_fragment";

    private boolean isRemembered;
    private FragmentSchedulesBinding binding;
    private int months, years;
    private String startingDate;
    private List<ScheduleCalendarModel.Datum> eventList;
    int goalId;
    String NAme;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSchedulesBinding.inflate(inflater, container, false);
        isRemembered = userSessionManager.isRemembered();
        fetchProjects();


        binding.calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                // Handle the selected date and display events for that date
                // You can show events in a dialog, a separate activity, or update a separate view
                // based on your implementation
                String selectedDate = date.getYear() + "-" + String.format("%02d", date.getMonth() + 1) + "-" + String.format("%02d", date.getDay());
                displayEventsForDate(selectedDate);
            }

            private void displayEventsForDate(String selectedDate) {
                // Retrieve events for the selected date from your data source
                List<Event> events = getEventsForDate(selectedDate);

                // Show the events in a dialog or update a separate view
                // You can customize this part based on your desired UI and layout
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Events for " + selectedDate);

                if (events.isEmpty()) {
                    builder.setMessage("No events found for this date.");
                } else {
                    StringBuilder message = new StringBuilder();
                    for (Event event : events) {
                        message.append("- ").append(event.getTitle()).append("\n");
                    }
                    builder.setMessage(message.toString());
                }

                builder.setPositiveButton("OK", null);
                builder.show();
            }

            private List<Event> getEventsForDate(String selectedDate) {
                // Create a list to store the events
                List<Event> events = new ArrayList<>();

                // Check if eventList is null
                if (eventList == null) {
                    return events; // Return an empty list
                }

                // Iterate over all events and add the ones that match or fall within the selected date range
                for (ScheduleCalendarModel.Datum event : eventList) {
                    String startDate = event.getStart_date();
                    String endDate = event.getEnd_date();

                    // Compare selectedDate with the start and end dates
                    if (selectedDate.equals(startDate) || selectedDate.equals(endDate) ||
                            (selectedDate.compareTo(startDate) > 0 && selectedDate.compareTo(endDate) < 0)) {
                        events.add(new Event(event.getTitle(), "blue"));
                    }
                }

                return events;
            }


        });

        binding.calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date) {
                fetchDataForMonth(date.getMonth() + 1, date.getYear());
            }

            private void fetchDataForMonth(int month, int year) {
                System.out.println("month:-" + month);
                System.out.println("year:-" + year);

                AndroidNetworking
                        .post(BASE_URL + PLANNER_SCHEDULE_CALENDAR_LIST)
                        .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                        .addBodyParameter("goal_id", String.valueOf(goalId))
                        .addBodyParameter("month", String.valueOf(month))
                        .addBodyParameter("year", String.valueOf(year))
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {

                                binding.calendarView.removeDecorators();

                                populateEventList(response);
                                // Rest of the code...
                            }

                            private void populateEventList(JSONObject response) {
                                try {
                                    int success = response.getInt("success");
                                    if (success == 1) {

                                        JSONArray eventsArray = response.getJSONArray("data");

                                        eventList = new ArrayList<>();

                                        for (int i = 0; i < eventsArray.length(); i++) {
                                            JSONObject eventObject = eventsArray.getJSONObject(i);
                                            ScheduleCalendarModel.Datum event = new ScheduleCalendarModel.Datum();
                                            event.setTitle(eventObject.getString("title"));
                                            event.setStart_date(eventObject.getString("start_date"));
                                            event.setEnd_date(eventObject.getString("end_date"));
                                            System.out.println("eventList:-" + eventList);

                                            eventList.add(event);

                                            String[] startDateParts = event.getStart_date().split("-");
                                            int startYear = Integer.parseInt(startDateParts[0]);
                                            int startMonth = Integer.parseInt(startDateParts[1]);
                                            int startDay = Integer.parseInt(startDateParts[2]);
                                            CalendarDay calendarDay = CalendarDay.from(startYear, startMonth - 1, startDay);
                                            Drawable highlightBackground = ContextCompat.getDrawable(getContext(), android.R.color.holo_blue_bright);
                                            EventDecorator eventDecorator = new EventDecorator(calendarDay, highlightBackground);
                                            binding.calendarView.addDecorator(eventDecorator);
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    // Handle JSON parsing error
                                }
                            }


                            @Override
                            public void onError(ANError anError) {
                                Log.e(TAG, "onError: " + anError.getErrorCode());
                                Log.e(TAG, "onError: " + anError.getErrorDetail());
                                Log.e(TAG, "onError: " + anError.getErrorBody());
                            }
                        });
            }

        });


        return binding.getRoot();

    }


    private void fetchProjects() {
        AndroidNetworking.post(BASE_URL + PLANNER_PROJECT_SPINNER_LIST)
                .addQueryParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");

                            if (success == 1) {
                                JSONArray dataArray = response.getJSONArray("data");
                                List<ProjectSpinnerModel> projectList = new ArrayList<>();

                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject projectObj = dataArray.getJSONObject(i);
                                    int id = projectObj.getInt("id");
                                    String name = projectObj.getString("name");

                                    ProjectSpinnerModel project = new ProjectSpinnerModel();
                                    project.setId(id);
                                    project.setName(name);
                                    projectList.add(project);
                                }

                                ArrayAdapter<ProjectSpinnerModel> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_form_type, projectList);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                binding.spnProjectSpinner.setAdapter(adapter);


                                // Set listener for item selection
                                binding.spnProjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        // Get the selected project ID
                                        int selectedProjectId = projectList.get(position).getId();

                                        // Pass the selected project ID to the next API method
                                        fetchAim(selectedProjectId);
                                        binding.clAimSpinner.setVisibility(View.VISIBLE);
                                        binding.spnAimSpinner.setAdapter(null);
                                        binding.spnGoalSpinner.setAdapter(null);
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                        // Handle when nothing is selected
                                        binding.clAimSpinner.setVisibility(View.GONE);
                                        binding.clGoalSpinner.setVisibility(View.GONE);

                                    }
                                });
                            } else {
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponseError: " + e.getLocalizedMessage());
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorCode());
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                        Log.e(TAG, "onError: " + anError.getErrorBody());
                    }
                });
    }

    private void fetchAim(int selectedProjectId) {

        AndroidNetworking.post(BASE_URL + PLANNER_AIM_SPINNER_LIST)
                .addQueryParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addQueryParameter("project_id", String.valueOf(selectedProjectId))
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {


                        try {
                            System.out.println("Response" + response);
                            int success = response.getInt("success");
                            String message = response.getString("message");

                            if (success == 1) {
                                JSONArray dataArray = response.getJSONArray("data");
                                System.out.println("Aim Response:- " + dataArray);
                                if (dataArray.length() == 0) {
                                    binding.clAimSpinner.setVisibility(View.GONE);
                                    binding.clGoalSpinner.setVisibility(View.GONE);
                                } else {
                                    List<ProjectSpinnerModel> projectList = new ArrayList<>();
                                    for (int i = 0; i < dataArray.length(); i++) {
                                        JSONObject projectObj = dataArray.getJSONObject(i);
                                        int id = projectObj.getInt("id");
                                        String aim = projectObj.getString("aim");
                                        ProjectSpinnerModel project = new ProjectSpinnerModel();
                                        project.setId(id);
                                        project.setName(aim);
                                        projectList.add(project);
                                    }
                                    ArrayAdapter<ProjectSpinnerModel> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_form_type, projectList);
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    binding.spnAimSpinner.setAdapter(adapter);
                                    // Set listener for item selection
                                    binding.spnAimSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                            // Get the selected project ID
                                            int selectedProjectId = projectList.get(position).getId();

                                            // Pass the selected project ID to the next API method
                                            fetchGoal(selectedProjectId);
                                            binding.clGoalSpinner.setVisibility(View.VISIBLE);


                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) {
                                            binding.clGoalSpinner.setVisibility(View.VISIBLE);
                                            // Handle when nothing is selected
                                        }
                                    });
                                }
                            } else {
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponseError: " + e.getLocalizedMessage());
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorCode());
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                        Log.e(TAG, "onError: " + anError.getErrorBody());
                    }
                });
    }

    private void fetchGoal(int selectedProjectId) {
        AndroidNetworking.post(BASE_URL + PLANNER_GOAL_SPINNER_LIST)
                .addQueryParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addQueryParameter("aim_id", String.valueOf(selectedProjectId))
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            System.out.println("Response" + response);
                            int success = response.getInt("success");
                            String message = response.getString("message");

                            if (success == 1) {
                                JSONArray dataArray = response.getJSONArray("data");
                                List<ProjectSpinnerModel> projectList = new ArrayList<>();

                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject projectObj = dataArray.getJSONObject(i);
                                    int id = projectObj.getInt("id");
                                    String goal = projectObj.getString("goal");

                                    ProjectSpinnerModel project = new ProjectSpinnerModel();
                                    project.setId(id);
                                    project.setName(goal);
                                    projectList.add(project);
                                }


                                ArrayAdapter<ProjectSpinnerModel> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_form_type, projectList);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                binding.spnGoalSpinner.setAdapter(adapter);

                                binding.spnGoalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        // Get the selected project ID
                                        goalId = projectList.get(position).getId();

                                        // Pass the selected project ID to the next API method
//                                        fetchAim(goalId);

                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                        // Handle when nothing is selected


                                    }
                                });

                            } else {
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponseError: " + e.getLocalizedMessage());
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorCode());
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                        Log.e(TAG, "onError: " + anError.getErrorBody());
                    }
                });
    }

    public class Event {
        private String title;
        private String color;

        public Event(String title, String color) {
            this.title = title;
            this.color = color;
        }

        public String getTitle() {
            return title;
        }

        public String getColor() {
            return color;
        }
    }

    private class EventDecorator implements DayViewDecorator {
        private CalendarDay date;
        private Drawable backgroundDrawable;

        public EventDecorator(CalendarDay date, Drawable backgroundDrawable) {
            this.date = date;
            this.backgroundDrawable = backgroundDrawable;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return date.equals(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setBackgroundDrawable(backgroundDrawable);
        }
    }

}

//            private void fetchDataForMonth(int i) {
//
//
//                AndroidNetworking
//                        .post(BASE_URL + PLANNER_SCHEDULE_CALENDAR_LIST)
//                        .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
//                        .addBodyParameter("goal_id", "137")
//                        .addBodyParameter("month", String.valueOf(months))
//                        .addBodyParameter("year", String.valueOf(years))
//                        .build()
//                        .getAsJSONObject(new JSONObjectRequestListener() {
//                            @Override
//                            public void onResponse(JSONObject response) {
//                                try {
//                                    int success = response.getInt("success");
//                                    String message = response.getString("message");
//
//                                    if (success == 1) {
//                                        JSONArray dataArray = response.getJSONArray("data");
//
////                                        JSONArray medicines = data.getJSONArray("medicines");
//
//                                        List<ScheduleCalendarModel.Datum> uList = new ArrayList<>();
//                                        for (int i = 0; i < dataArray.length(); i++) {
//                                            JSONObject medicine = dataArray.getJSONObject(i);
//
//                                            ScheduleCalendarModel.Datum userItem = new ScheduleCalendarModel.Datum();
//                                            userItem.setTitle(medicine.getString("title"));
//                                            userItem.setStart_date(medicine.getString("start_date"));
//
//
//
//                                            NAme = userItem.getTitle();
//                                            startingDate = userItem.getStart_date();
//
//
//
//                                            uList.add(userItem);
////                                    adapter.addUserList(userList);
//
//                                        }
//
//                                    } else {
//                                        String errorMessage = "API request unsuccessful: " + message;
//                                        // Handle the error message
//                                    }
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                    // Handle JSON parsing error
//                                }
//
//                            }
//
//                            @Override
//                            public void onError(ANError anError) {
//                                Log.e(TAG, "onError: " + anError.getErrorCode());
//                                Log.e(TAG, "onError: " + anError.getErrorDetail());
//                                Log.e(TAG, "onError: " + anError.getErrorBody());
//
//                            }
//                        });
//
//
//            }

