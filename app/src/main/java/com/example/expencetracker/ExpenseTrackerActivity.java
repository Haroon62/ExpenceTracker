package com.example.expencetracker;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class ExpenseTrackerActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_tracker);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Initialize Firebase Database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        databaseReference = database.getReference("expenses");

        // Initialize UI elements
        EditText descriptionEditText = findViewById(R.id.edit_text_description);
        EditText amountEditText = findViewById(R.id.edit_text_amount);
        EditText categoryEditText = findViewById(R.id.edit_text_category);
        Button addExpenseButton = findViewById(R.id.button_add_expense);
        TextView expenseSummaryTextView = findViewById(R.id.text_view_expense_summary);

        addExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user inputs for expense details
                String description = descriptionEditText.getText().toString().trim();
                double amount = Double.parseDouble(amountEditText.getText().toString());
                String category = categoryEditText.getText().toString().trim();

                // Add the expense to Firebase
                addExpense(description, amount, category);
            }
        });

        // Display expense summary from Firebase
        getExpenseSummary(expenseSummaryTextView);
    }

    private void addExpense(String description, double amount, String category) {
        // Generate a unique key for the new expense
        String expenseId = databaseReference.push().getKey();

        // Create an Expense object
        Expense expense = new Expense(description, amount, category);

        // Save the expense to Firebase under a unique ID
        databaseReference.child(expenseId).setValue(expense);
    }

    private void getExpenseSummary(TextView expenseSummaryTextView) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                StringBuilder summary = new StringBuilder();
                for (DataSnapshot expenseSnapshot : dataSnapshot.getChildren()) {
                    Expense expense = expenseSnapshot.getValue(Expense.class);
                    summary.append(expense.toString()).append("\n");
                }
                expenseSummaryTextView.setText(summary.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }

    // Expense class to represent individual expenses

}
