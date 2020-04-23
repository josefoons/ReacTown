package es.josefons.reactown;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterFragment extends Fragment {

    private EditText etNombre, etCorreo, etPass;
    private Button btnRegistrar;
    private TextView alreadyAccount;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etNombre = view.findViewById(R.id.etNombreRegistro);
        etCorreo = view.findViewById(R.id.etCorreoRegistro);
        etPass = view.findViewById(R.id.etPasswordRegistro);
        btnRegistrar = view.findViewById(R.id.btnRegistro);
        alreadyAccount = view.findViewById(R.id.tvYaCuenta);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = etNombre.getText().toString();
                String correo = etCorreo.getText().toString();
                String pass = etPass.getText().toString();

                if(!nombre.isEmpty() && !correo.isEmpty() && !pass.isEmpty()){
                    if(pass.length()  >= 6) {
                        registrarUsuario(correo, pass);
                    }
                }
            }
        });

        alreadyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.registroCompleto);
            }
        });
    }

    private void registrarUsuario(String correo, String pass){
        mAuth.createUserWithEmailAndPassword(correo, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {

                    Map<String, Object> map = new HashMap<>();
                    map.put("name", etNombre.getText().toString());
                    map.put("correo", etCorreo.getText().toString());
                    map.put("password", etPass.getText().toString());
                    map.put("perm", 0);

                    String id = mAuth.getCurrentUser().getUid();
                    mDatabase.child("Users").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if(task2.isSuccessful()) {
                                Navigation.findNavController(getView()).navigate(R.id.registroCompleto);
                            } else {
                                Toast.makeText(getView().getContext(), "Fallo al registrar", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
