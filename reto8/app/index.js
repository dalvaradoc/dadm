import { Platform, ScrollView, StyleSheet, Text, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import * as SQLite from "expo-sqlite";
import { useEffect, useState } from "react";
import { FAB } from "react-native-paper";
import { router } from 'expo-router';

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

function Items({ onPressItem }) {
  const [items, setItems] = useState(null);

  useEffect(() => {
    db.transaction((tx) => {
      tx.executeSql(
        `select * from contacts;`,
        (_, { rows: { _array } }) => setItems(_array)
      );
    });
    console.log("retrieving rows...");
  }, []);

  if (items === null || items.length === 0) {
    return null;
  }

  return (
    <View style={styles.sectionContainer}>
      <Text style={styles.sectionHeading}>Contacts</Text>
      {items.map(({ id, name, url, phone, p_s, classi }) => (
        <TouchableOpacity
          key={id}
          onPress={() => onPressItem && onPressItem(id)}
          style={{
            backgroundColor: "#fff",
            borderColor: "#000",
            borderWidth: 1,
            padding: 8,
          }}
        >
          <Text style={{ color:  "#000" }}>{name}</Text>
        </TouchableOpacity>
      ))}
    </View>
  );
}

export default function App() {

  useEffect(() => {
    db.transaction((tx) => {
      tx.executeSql(
        'create table if not exists contacts ( id	INTEGER NOT NULL, name TEXT NOT NULL, url TEXT NOT NULL, phone NUMERIC NOT NULL, products_services	TEXT NOT NULL, classification	TEXT NOT NULL CHECK(classification = "con" OR classification = "des" OR classification = "fab"), PRIMARY KEY(id AUTOINCREMENT));'
      );
    },() => console.log(err), () => console.log("Table created"));
  }, []);

  return (
    <SafeAreaView style={styles.container}>
      <Text style={styles.title}>Contacts App</Text>
      <Items  />
      <FAB style={styles.fab} icon="plus" onPress={() => router.push("/createForm")} />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    backgroundColor: "#fff",
    flex: 1,
    alignItems: "center"
  },
  title: {
    fontSize: 30,
    fontWeight: "bold"
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
    padding: 8,
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
