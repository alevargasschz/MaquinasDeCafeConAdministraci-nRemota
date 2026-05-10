package interfaces;

import java.io.*;
import java.util.*;

public abstract class Repositorio<K extends Serializable, T extends Serializable> implements Serializable {

    private HashMap<K, T> elements;
    private String name;

    public Repositorio(String name) {
        elements = new HashMap<>();
        this.name = name;
        loadData();
    }

    /**
     * note que este metodo tambien sirve para setear el valor de un elemento cuando
     * k ya esta en la hashmap.
     */
    public void addElement(K key, T element) {
        elements.put(key, element);
        this.saveData();
    }

    public void removeElement(K key) {
        elements.remove(key);
    }

    public HashMap<K, T> getElements() {
        return elements;
    }

    public void setElements(HashMap<K, T> elements) {
        this.elements = elements;
    }

    public T findByKey(K key) {

        return elements.get(key);

    }

    public void saveData() {
        try {
            FileOutputStream fos = new FileOutputStream(name);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(this);
            oos.close();
        } catch (FileNotFoundException ffe) {
            System.out.println("error al guardar FileNotFound");
            ffe.printStackTrace();
        } catch (Exception e) {
            System.out.println("error al guardar");

            e.printStackTrace();
        }
    }

    public List<T> getValues() {
        return new ArrayList<>(elements.values());
    }

    public List<K> getkeys() {
        List<K> answer = new ArrayList<>();
        Iterator<K> keys = elements.keySet().iterator();
        while (keys.hasNext()) {
            answer.add(keys.next());
        }
        return answer;
    }

    public void loadData() {
        FileInputStream fis;
        try {

            File f = new File(name);
            if (!f.exists()) {
                System.out.println("archivo no encontrado");
                loadDataP();
                return;
            }

            fis = new FileInputStream(f);
            ObjectInputStream oos = new ObjectInputStream(fis);

            Repositorio<K, T> md = (Repositorio<K, T>) oos.readObject();
            this.setElements(md.getElements());
            oos.close();
        } catch (Exception e) {
            System.err.println("Error al cargar AlarmaRepositorio");
            e.printStackTrace();
        }

    }

    public abstract void loadDataP();

}
