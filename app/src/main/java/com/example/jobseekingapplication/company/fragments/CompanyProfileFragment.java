package com.example.jobseekingapplication.company.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.example.jobseekingapplication.R;
import com.example.jobseekingapplication.model.Company;
import com.example.jobseekingapplication.utils.SharedPrefManager;

public class CompanyProfileFragment extends Fragment {

    Context context;
    TextView name, email, address, details;
    Button update;
    NavController navController;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public CompanyProfileFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_company_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        Company company= SharedPrefManager.getInstance(context).getCompanyData();
        name = view.findViewById(R.id.company_name);

        email = view.findViewById(R.id.email);
        address = view.findViewById(R.id.company_address);
        details = view.findViewById(R.id.company_details);

        name.setText(company.getCompanyName());
        email.setText(company.getEmail());
        address.setText(company.getCompanyAddress());
        details.setText(company.getCompanyDetails());

        update = view.findViewById(R.id.update);
        update.setOnClickListener(v->{
            navController.navigate(R.id.action_ToUpdateProfile);
        });
    }
}