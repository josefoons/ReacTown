package es.josefons.reactown.ui.main;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

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
import com.google.firebase.iid.Registrar;

import es.josefons.reactown.R;

public class MainFragment extends Fragment {

    private MainViewModel mViewModel;
    private EditText etCorreo, etPass;
    private Button btnLogin;
    private TextView textoRegistro, textoOlvidado;
    private View vista;

    private String email = "";
    private String password = "";

    private FirebaseAuth mAuth;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        vista = container;
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();

        etCorreo = getView().findViewById(R.id.etMailLogin);
        etPass = getView().findViewById(R.id.etPasswordLogin);
        btnLogin = getView().findViewById(R.id.btnLoguear);
        textoRegistro = getView().findViewById(R.id.tvRegister);
        textoOlvidado = getView().findViewById(R.id.tvPassOlvidada);
        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = etCorreo.getText().toString();
                password = etPass.getText().toString();

                if(checkInternet()){
                    if(!email.isEmpty() && !password.isEmpty()){
                        loginUser();
                    }
                } else {
                    Toast.makeText(vista.getContext(), "Completa los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        textoRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkInternet()){
                    Navigation.findNavController(v).navigate(R.id.mainToRegistro);
                }
            }
        });

        textoOlvidado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkInternet()){
                    Navigation.findNavController(v).navigate(R.id.irRecuperar);
                }
            }
        });
    }

    private void loginUser(){
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    if (mAuth.getCurrentUser().isEmailVerified()) {
                        if(Navigation.findNavController(vista).getCurrentDestination().getId() == R.id.mainFragment){
                            Navigation.findNavController(vista).navigate(R.id.loginCompleto);
                        }
                    } else {
                        Toast.makeText(getContext(), "Autentifica tu cuenta en el correo.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(vista.getContext(), "Fallo al loguear", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (mAuth.getCurrentUser() == null) {
                    // Evitar que haga nada...
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
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
}
