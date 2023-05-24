package com.safra.fragments;

import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.PLANNER_AIM_SPINNER_LIST;
import static com.safra.utilities.Common.PLANNER_BUDGET_LIST;
import static com.safra.utilities.Common.PLANNER_GOAL_SPINNER_LIST;
import static com.safra.utilities.Common.PLANNER_PROJECT_SPINNER_LIST;
import static com.safra.utilities.Common.PLANNER_TASK_SPINNER_LIST;
import static com.safra.utilities.UserSessionManager.userSessionManager;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.safra.R;
import com.safra.Safra;
import com.safra.adapters.BudgetRecyclerAdapter;
import com.safra.databinding.FragmentBudgetBinding;
import com.safra.databinding.FragmentSchedulesBinding;
import com.safra.models.BudgetListModel;
import com.safra.models.ProjectSpinnerModel;
import com.safra.utilities.SpaceItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class BudgetFragment extends Fragment {
    public static final String TAG = "budget_fragment";

    private boolean isRemembered;
    private FragmentBudgetBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentBudgetBinding.inflate(inflater, container, false);
        isRemembered = userSessionManager.isRemembered();
        fetchProjects();

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
                                        binding.spnTaskSpinner.setAdapter(null);
                                        binding.clGoalSpinner.setVisibility(View.GONE);
                                        binding.clTaskSpinner.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                        // Handle when nothing is selected
                                        binding.clAimSpinner.setVisibility(View.GONE);
                                        binding.clGoalSpinner.setVisibility(View.GONE);
                                        binding.clTaskSpinner.setVisibility(View.GONE);
                                        binding.clButtons.setVisibility(View.GONE);
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
                            int success = response.getInt("success");
                            String message = response.getString("message");

                            if (success == 1) {
                                JSONArray dataArray = response.getJSONArray("data");
                                if (dataArray.length() == 0) {
                                    binding.clAimSpinner.setVisibility(View.GONE);
                                    binding.clGoalSpinner.setVisibility(View.GONE);
                                    binding.clTaskSpinner.setVisibility(View.GONE);
                                    binding.clButtons.setVisibility(View.GONE);
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
                                if (dataArray.length() == 0) {
                                    binding.clGoalSpinner.setVisibility(View.GONE);
                                    binding.clTaskSpinner.setVisibility(View.GONE);
                                } else {
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
                                            int selectedProjectId = projectList.get(position).getId();

                                            // Pass the selected project ID to the next API method
                                            fetchTask(selectedProjectId);
                                            binding.clTaskSpinner.setVisibility(View.VISIBLE);


                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) {
                                            binding.clTaskSpinner.setVisibility(View.VISIBLE);
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

    private void fetchTask(int selectedProjectId) {
        System.out.println("FetchTask APi Call");
        AndroidNetworking.post(BASE_URL + PLANNER_TASK_SPINNER_LIST)
                .addQueryParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addQueryParameter("goal_id", String.valueOf(selectedProjectId))
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
                                if (dataArray.length() == 0) {

                                    binding.clTaskSpinner.setVisibility(View.GONE);
                                    binding.clButtons.setVisibility(View.GONE);
                                } else {
                                    List<ProjectSpinnerModel> projectList = new ArrayList<>();
                                    for (int i = 0; i < dataArray.length(); i++) {
                                        JSONObject projectObj = dataArray.getJSONObject(i);
                                        int id = projectObj.getInt("id");
                                        String title = projectObj.getString("title");
                                        ProjectSpinnerModel project = new ProjectSpinnerModel();
                                        project.setId(id);
                                        project.setName(title);
                                        projectList.add(project);
                                    }


                                    ArrayAdapter<ProjectSpinnerModel> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_form_type, projectList);
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    binding.spnTaskSpinner.setAdapter(adapter);
                                    binding.spnTaskSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                            // Get the selected project ID
                                            int selectedProjectId = projectList.get(position).getId();

                                            // Pass the selected project ID to the next API method

                                            binding.clButtons.setVisibility(View.VISIBLE);
                                            binding.btnSave.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    SelectTask(selectedProjectId);

                                                }
                                            });

                                        }



                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) {
                                            binding.clButtons.setVisibility(View.VISIBLE);
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

    private void SelectTask(int selectedProjectId) {
        System.out.println("FetchTask API Call");
        AndroidNetworking.post(BASE_URL + PLANNER_BUDGET_LIST)
                .addQueryParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addQueryParameter("task_id", String.valueOf(selectedProjectId))
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray dataArray = response.getJSONArray("data");
                            List<BudgetListModel.Datum> taskList = new ArrayList<>();
                            double totalSubtotal = 0.0; // Variable to hold the sum of subtotals
                            binding.clTotal.setVisibility(View.VISIBLE);

                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject taskObj = dataArray.getJSONObject(i);
                                int id = taskObj.getInt("id");
                                int plannerTaskId = taskObj.getInt("planner_task_id");
                                String name = taskObj.getString("name");
                                int quantity = taskObj.getInt("quantity");
                                double price = taskObj.getDouble("price");
                                double subTotal = taskObj.getDouble("subtotal");
                                DecimalFormat decimalFormat = new DecimalFormat("#.00");
                                String formattedNumber = decimalFormat.format(subTotal);
                                System.out.println("Total Subtotal: " + formattedNumber);

                                BudgetListModel.Datum task = new BudgetListModel.Datum();
                                task.setId(id);
                                task.setPlanner_task_id(plannerTaskId);
                                task.setName(name);
                                task.setQuantity(quantity);
                                task.setPrice(price);
                                task.setSubtotal(Double.parseDouble(formattedNumber));



                                taskList.add(task);

                                // Add the current subtotal to the totalSubtotal variable
                                totalSubtotal += subTotal;
                            }

                            DecimalFormat decimalFormat = new DecimalFormat("#.00");
                            String formattedNumber = decimalFormat.format(totalSubtotal);
                            // Display the totalSubtotal
                            binding.tvTotal.setText(String.valueOf(formattedNumber + " MZN"));


                            // Display the taskList using RecyclerView
                            BudgetRecyclerAdapter adapter = new BudgetRecyclerAdapter(taskList);
                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                            binding.innerRecyclerView.setLayoutManager(layoutManager);
                            binding.innerRecyclerView.addItemDecoration(new SpaceItemDecoration(getContext(), RecyclerView.VERTICAL,
                                    1, R.dimen._5dp, R.dimen._0dp, false));
                            binding.innerRecyclerView.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
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




}