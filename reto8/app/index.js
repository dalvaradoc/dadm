import {
  FlatList,
  Platform,
  ScrollView,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { useCallback, useEffect, useState } from "react";
import { Checkbox, FAB, SegmentedButtons, TextInput } from "react-native-paper";
import { router } from "expo-router";
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

const B = (props) => (
  <Text style={{ fontWeight: "bold" }}>{props.children}</Text>
);

function Items() {
  const [items, setItems] = useState(null);
  const [searchName, setnameSearch] = useState("");
  const [filterClass, setfilterClass] = useState(null);

  function generateQuery() {
    let query = 'SELECT * FROM contacts WHERE name LIKE "%' + searchName + '%"';
    if (filterClass != null)
      query += ' AND classification = "' + filterClass + '"';
    console.log(query);
    return query;
  }

  function handleFilter(text) {
    filterClass == null || filterClass != text
      ? setfilterClass(text)
      : setfilterClass(null);
  }

  useEffect(() => {
    db.transaction((tx) => {
      tx.executeSql(
        generateQuery(),
        [],
        (trans, res) => setItems(res.rows["_array"]),
        () => console.log("ok!"),
        () => console.log("error retreaving rows")
      );
    });
    console.log("retrieving rows...");
  }, [searchName, filterClass]);

  const classi = {
    con: "Consultoría",
    des: "Dessarrollo a la medida",
    fab: "Fábrica de software",
  };

  const renderItem = useCallback(({ item }) => (
    <TouchableOpacity
      key={item.id}
      onPress={() => router.push("/editForm/" + item.id)}
      style={{
        backgroundColor: "#eee",
        borderRadius: 10,
        padding: 15,
        marginBottom: 15,
        shadowColor: "#000",
        shadowOffset: {
          width: 0,
          height: 2,
        },
        shadowOpacity: 0.25,
        shadowRadius: 3.84,
        elevation: 5,
      }}
    >
      <Text
        style={{
          color: "#555",
          fontWeight: "bold",
          fontSize: 20,
          marginBottom: 5,
        }}
      >
        {item.name}
      </Text>
      <Text style={{ color: "#000" }}>
        <B>URL: </B> {item.url}
      </Text>
      <Text style={{ color: "#000" }}>
        <B>Teléfono: </B> {item.phone}
      </Text>
      <Text style={{ color: "#000" }}>
        <B>Email: </B> {item.email}
      </Text>
      <Text style={{ color: "#000" }}>
        <B>Productos/Servicios: </B> {item.products_services}
      </Text>
      <Text style={{ color: "#000" }}>
        <B>Clasificación: </B> {classi[item.classification]}
      </Text>
    </TouchableOpacity>
  ), []);

  return (
    <View style={styles.sectionContainer}>
      <TextInput
        label="Buscar por nombre"
        value={searchName}
        onChangeText={(text) => setnameSearch(text)}
        style={{
          marginBottom: 15,
        }}
      />
      <SegmentedButtons
        value={filterClass}
        style={{
          marginBottom: 15,
        }}
        onValueChange={(text) => handleFilter(text)}
        buttons={[
          {
            value: "con",
            label: "Consultoría",
          },
          {
            value: "des",
            label: "Desarrollo a la medida",
          },
          { value: "fab", label: "Fábrica de software" },
        ]}
      />
      {items === null || items.length === 0 ? (
        <></>
      ) : (
        <FlatList
          data={items}
          style={{
            flex: 1,
          }}
          windowSize={3}
          renderItem={renderItem}
        />
      )}
    </View>
  );
}

export default function App() {
  useEffect(() => {
    db.transaction(
      (tx) => {
        tx.executeSql(
          'create table if not exists contacts (id	INTEGER NOT NULL, name	TEXT NOT NULL, url	TEXT NOT NULL,phone	NUMERIC NOT NULL, email	TEXT NOT NULL, products_services	TEXT NOT NULL, classification TEXT NOT NULL CHECK(classification = "con" OR classification = "des" OR classification = "fab"), PRIMARY KEY(id AUTOINCREMENT));'
          // "DROP TABLE contacts"
        );
      },
      () => console.log("error"),
      () => console.log("Table created")
    );
  }, []);

  return (
    <View
      style={{
        flex: 1,
      }}
    >
      <AppBar title={"Contactos"} />
      <Items />
      <FAB
        style={styles.fab}
        icon="plus"
        onPress={() => router.push("/createForm")}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    backgroundColor: "#fff",
    flex: 1,
    padding: 10,
    // alignItems: "center"
  },
  title: {
    fontSize: 30,
    fontWeight: "bold",
    textAlign: "center",
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
    display: "flex",
    flexDirection: "column",
    flexGrow: 1,
  },
  sectionHeading: {
    fontSize: 18,
    marginBottom: 8,
  },
  fab: {
    position: "absolute",
    right: 0,
    bottom: 0,
    margin: 16,
  },
});
