package com.alura.screenmatch.principal;

import com.alura.screenmatch.excepcion.ErrorEnConvercionDeDuracionExepcion;
import com.alura.screenmatch.modelos.Titulo;
import com.alura.screenmatch.modelos.TituloOMDB;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PrincipalConBusqueda {

    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner lectura = new Scanner(System.in);
        List<Titulo> titulos = new ArrayList<>();
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .setPrettyPrinting()
                .create();

        while(true){
            System.out.println("Escribe el nombre de la pelicula: ");
            var busqueda = lectura.nextLine();

            if (busqueda.equalsIgnoreCase("salir")){
                break;
            }

            String direccion = "http://www.omdbapi.com/?apikey=af6174de&t=" +
                    URLEncoder.encode(busqueda, StandardCharsets.UTF_8);

            try {
                HttpClient client = HttpClient.newHttpClient();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(direccion))
                        .build();

                HttpResponse<String> response = client
                        .send(request, HttpResponse.BodyHandlers.ofString());

                String json = response.body();
                System.out.println(json);

                TituloOMDB mitituloOMDB = gson.fromJson(json, TituloOMDB.class);
                System.out.println(mitituloOMDB);


                Titulo mititulo = new Titulo(mitituloOMDB);
                System.out.println("Titulo ya convertido: " + mititulo);
                titulos.add(mititulo);
            }catch (NumberFormatException e){
                System.out.println("Ocurrio un error: ");
                System.out.println(e.getMessage());
            }catch (IllegalArgumentException e){
                System.out.println("Error en la URL, verifique la direccon");
            }catch (ErrorEnConvercionDeDuracionExepcion e){
                System.out.println(e.getMensaje());
            }
        }
        System.out.println(titulos);

        FileWriter escritura = new FileWriter ("titulos.json");
        escritura.write(gson.toJson(titulos));
        escritura.close();
        System.out.println("Finalizo la ejecucion del programa ");
    }
}
