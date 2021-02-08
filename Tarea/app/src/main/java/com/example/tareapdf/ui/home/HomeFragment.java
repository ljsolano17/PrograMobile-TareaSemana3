package com.example.tareapdf.ui.home;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.tareapdf.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.File;
import java.io.IOException;


import static android.content.Context.DOWNLOAD_SERVICE;

public class HomeFragment extends Fragment {

    private static final int PERMISSION_STORAGE_CODE=1;
    private EditText etArchivo;

    private Button btDescarga;
    private Button btBusca;
    private Button btCarga;
    private StorageReference storageReference;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);



        etArchivo = root.findViewById(R.id.etArchivo);
        btDescarga = root.findViewById(R.id.btDescarga);
        btBusca = root.findViewById(R.id.btBuscar);
        btCarga = root.findViewById(R.id.btCargar);

        btDescarga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    descarga();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        btBusca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                busca();
            }
        });
        btCarga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carga();
            }
        });
        return root;
    }
  /*  StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    StorageReference islandRef;*/
    private void descarga() throws IOException {

        String url = etArchivo.getText().toString();

        storageReference = FirebaseStorage
                .getInstance().getReference().child(url);

        File localFile = null;
        //Try catch para errores como los de espacio insuficiente...
        try {
           localFile = File.createTempFile("images","pdf");
        } catch (IOException e) {
            e.printStackTrace();
        }

        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                DownloadManager.Request request = new DownloadManager.Request(
                        Uri.parse(uri.toString()));

                String cookies = CookieManager.getInstance().getCookie(url);
                request.addRequestHeader("cookie", cookies);

                request.setDescription("Downloading File...");
                request.setTitle(url);
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
               /* request.setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(
                                url, "tarea.pdf", "pdf"));*/
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,url);
                DownloadManager dm = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
                Toast.makeText(getContext(), "Downloading File", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"Err",Toast.LENGTH_SHORT).show();
            }
        });




    }
    private void busca() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/*");//Todas las imagenes sean png,jpg, entre otras

        startActivityForResult(intent,333);
    }

    private Uri uri;

    public void onActivityResult(int cod1, int cod2, Intent datos){
        uri = datos.getData();

    }
    private void carga() {
        String archivo = etArchivo.getText().toString();
        storageReference = FirebaseStorage
                .getInstance().getReference().child(archivo);
        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getContext(),"Se subió",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"Error de subida",Toast.LENGTH_SHORT).show();
            }
        });
    }
}