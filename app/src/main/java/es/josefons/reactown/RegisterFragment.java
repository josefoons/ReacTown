package es.josefons.reactown;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import java.security.MessageDigest;
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

    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
        return fragment;
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

                if(checkInternet()) {
                    if(!nombre.isEmpty() && !correo.isEmpty() && !pass.isEmpty()){
                        if(pass.length()  >= 6) {
                            registrarUsuario(correo, pass);
                        } else {
                            Toast.makeText(getContext(), "Contraseña de 6 o mas digitos", Toast.LENGTH_SHORT).show();
                        }
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
                    map.put("password", sha256(etPass.getText().toString()));
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

    private boolean checkInternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        } else {
            Toast.makeText(getContext(), "NO INTERNET. Revisa la conexion", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * Funcion de cifrado de la contraseña
     * @param base
     * @return
     */
    private static String sha256(String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
