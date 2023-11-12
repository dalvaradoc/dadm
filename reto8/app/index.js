import { Platform, ScrollView, StyleSheet, Text, TouchableOpacity, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { useEffect, useState } from "react";
import { FAB } from "react-native-paper";
import { router } from 'expo-router';
import * as SQLite from "expo-sqlite";
import AppBar from "../components/Appbar";

function openDatabase() {
  if (Platform.OS === "web") {
    return {
      transaction: () => {
        return {
          executeSql: () => {},
        };
      },
    };
  }

  const db = SQLite.openDatabase("db.db");
  return db;
}

const db = openDatabase();

const B = (props) => <Text style={{fontWeight: 'bold'}}>{props.children}</Text>

function Items() {
  const [items, setItems] = useState(null);

  useEffect(() => {
    db.transaction((tx) => {
      tx.executeSql(
        `select * from contacts;`,[], (trans, res) => setItems(res.rows["_array"]),
        () => console.log("ok!"),
        () => console.log("error retreaving rows")
      );
    });
    console.log("retrieving rows...");
  }, []);

  if (items === null || items.length === 0) {
    return null;
  }

  const classi = {
    "con": "Consultoría",
    "des": "Dessarrollo a la medida",
    "fab": "Fábrica de software"
  }

  return (
    <View style={styles.sectionContainer}>
      {/* <Text style={styles.sectionHeading}>Contacts</Text> */}
      {items.map(({ id, name, url, phone, products_services, classification }) => (
        <TouchableOpacity
          key={id}
          onPress={() => router.push("/editForm/"+id)}
          style={{
            backgroundColor: "#fff",
            borderColor: "#00000055",
            borderBottomWidth: 1,
            padding: 8
          }}
        >
          <Text style={{ color:  "#000" }}><B>Name: </B> {name}</Text>
          <Text style={{ color:  "#000" }}><B>URL: </B> {url}</Text>
          <Text style={{ color:  "#000" }}><B>Phone: </B> {phone}</Text>
          <Text style={{ color:  "#000" }}><B>Products/Services: </B> {products_services}</Text>
          <Text style={{ color:  "#000" }}><B>Classification: </B> {classi[classification]}</Text>
        </TouchableOpacity>
      ))}
    </View>
  );
}

export default function App() {

  useEffect(() => {
    db.transaction((tx) => {
      tx.executeSql(
        'create table if not exists contacts (id	INTEGER NOT NULL, name	TEXT NOT NULL, url	TEXT NOT NULL,phone	NUMERIC NOT NULL, products_services	TEXT NOT NULL, classification TEXT NOT NULL CHECK(classification = "con" OR classification = "des" OR classification = "fab"), PRIMARY KEY(id AUTOINCREMENT));'
        // "DROP TABLE contacts"
      );
    },() => console.log("error"), () => console.log("Table created"));
  }, []);

  return (
    <>
      <AppBar title={"Contactos"} />
      <Items  />
      <FAB style={styles.fab} icon="plus" onPress={() => router.push("/createForm")} />
    </>
  );
}

const styles = StyleSheet.create({
  container: {
    backgroundColor: "#fff",
    flex: 1,
    padding: 10
    // alignItems: "center"
  },
  title: {
    fontSize: 30,
    fontWeight: "bold",
    textAlign: "center"
  },
  heading: {
    fontSize: 20,
    fontWeight: "bold",
    textAlign: "center",
  },
  flexRow: {
    flexDirection: "row",
  },
  input: {
    borderColor: "#4630eb",
    borderRadius: 4,
    borderWidth: 1,
    flex: 1,
    height: 48,
    margin: 16,
  },
  listArea: {
    backgroundColor: "#f0f0f0",
    flex: 1,
    paddingTop: 16,
  },
  sectionContainer: {
    marginBottom: 16,
    marginHorizontal: 16,
  },
  sectionHeading: {
    fontSize: 18,
    marginBottom: 8,
  },
  fab: {
    position: "absolute",
    right: 0,
    bottom: 0,
    margin: 16
  }
});