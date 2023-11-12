import * as React from "react";
import { StyleSheet, View } from "react-native";
import { Button, List, SegmentedButtons, TextInput } from "react-native-paper";
import AppBar from "../../components/Appbar";
import { router } from "expo-router";
import * as SQLite from "expo-sqlite";

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

export default function CreateForm() {
  const [formData, setFormData] = React.useState({
    name: "",
    url: "",
    phone: "",
    email: "",
    p_s: "",
    classi: "",
  });

  const handleChange = (name, value) => {
    setFormData((prevFormData) => ({ ...prevFormData, [name]: value }));
  };

  const hasErrors = () => {
    return formData.firstName.length === 1;
  };

  const createCompany = () => {
    db.transaction((tx) => {
      tx.executeSql(
        "INSERT INTO contacts (name, url, phone, email, products_services, classification) VALUES (?, ?, ?, ?, ?, ?)",
        [
          formData.name,
          formData.url,
          parseInt(formData.phone),
          formData.email,
          formData.p_s,
          formData.classi,
        ],
        () => router.back(),
        (trans, err) => {
          console.log(err);
          console.log(formData);
        }
      );
    });
  };

  return (
    <View>
      <AppBar title="Crear empresa" backUrl="/" />
      <View style={styles.container}>
        <TextInput
          label="Nombre de la empresa"
          value={formData.name}
          style={styles.textInput}
          onChangeText={(text) => handleChange("name", text)}
        />

        <TextInput
          label="URL"
          value={formData.url}
          style={styles.textInput}
          onChangeText={(text) => handleChange("url", text)}
        />
        <TextInput
          label="Teléfono"
          value={formData.phone}
          style={styles.textInput}
          onChangeText={(text) => handleChange("phone", text)}
        />
        <TextInput
          label="Email"
          value={formData.email}
          style={styles.textInput}
          onChangeText={(text) => handleChange("email", text)}
        />
        <TextInput
          label="Productos y servicios"
          value={formData.p_s}
          style={styles.textInput}
          onChangeText={(text) => handleChange("p_s", text)}
        />
        <SegmentedButtons
          value={formData.classi}
          style={{
            marginVertical: 10,
          }}
          onValueChange={(text) => handleChange("classi", text)}
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

        <Button
          style={{ marginTop: 20, maxWidth: 200 }}
          mode="contained"
          onPress={() => createCompany()}
        >
          Crear empresa
        </Button>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    paddingLeft: 30,
    paddingRight: 30,
  },
  textInput: {
    marginBottom: 5,
  },
});
