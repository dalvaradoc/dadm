import * as React from "react";
import { StyleSheet, View } from "react-native";
import { Button, List, SegmentedButtons, TextInput } from "react-native-paper";
import AppBar from "../../components/Appbar";
import { router, useLocalSearchParams } from "expo-router";
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

export default function EditForm() {
  const { id } = useLocalSearchParams();

  const [formData, setFormData] = React.useState({
    name: "",
    url: "",
    phone: "",
    products_services: "",
    classification: "",
  });

  React.useEffect(() => {
    db.transaction((tx) => {
      tx.executeSql(
        `select * from contacts where "id" = ?;`,
        [id],
        (trans, res) => {
          let item = res.rows["_array"][0];
          item.phone = item.phone.toString();
          setFormData(item);
        },
        () => console.log("ok!"),
        () => console.log("error retreaving rows")
      );
    });
    console.log("retrieving rows...");
  }, []);

  const handleChange = (name, value) => {
    setFormData((prevFormData) => ({ ...prevFormData, [name]: value }));
  };

  const hasErrors = () => {
    return formData.firstName.length === 1;
  };

  const updateCompany = () => {
    db.transaction((tx) => {
      tx.executeSql(
        "UPDATE contacts SET name = ?, url = ?, phone = ?, products_services = ?, classification = ? WHERE id = ?",
        [
          formData.name,
          formData.url,
          parseInt(formData.phone),
          formData.products_services,
          formData.classification,
          id
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
      <AppBar title="Editar empresa" backUrl="/" />
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
          label="Productos y servicios"
          value={formData.products_services}
          style={styles.textInput}
          onChangeText={(text) => handleChange("products_services", text)}
        />
        <SegmentedButtons
          value={formData.classification}
          onValueChange={(text) => handleChange("classification", text)}
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

        <View style={{ display: "flex", flexDirection: "row" }}>
          <Button
            style={{ marginTop: 20, width: "50%", borderRadius: 0 }}
            mode="contained"
            onPress={() => updateCompany()}
          >
            Guardar
          </Button>
          <Button
            style={{
              marginTop: 20,
              width: "50%",
              borderRadius: 0,
              backgroundColor: "red",
            }}
            mode="contained"
            onPress={() => updateCompany()}
          >
            Eliminar
          </Button>
        </View>
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
