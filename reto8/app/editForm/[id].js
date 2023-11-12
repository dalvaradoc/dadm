import * as React from "react";
import { StyleSheet, Text, View } from "react-native";
import {
  Button,
  Dialog,
  PaperProvider,
  Portal,
  SegmentedButtons,
  TextInput,
} from "react-native-paper";
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
    email: "",
    products_services: "",
    classification: "",
  });

  const [deleteDialogShow, setdeleteDialogShow] = React.useState(false);

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
        "UPDATE contacts SET name = ?, url = ?, phone = ?, email = ?, products_services = ?, classification = ? WHERE id = ?",
        [
          formData.name,
          formData.url,
          parseInt(formData.phone),
          formData.email,
          formData.products_services,
          formData.classification,
          id,
        ],
        () => router.back(),
        (trans, err) => {
          console.log(err);
          console.log(formData);
        }
      );
    });
  };

  function deleteCompany() {
    db.transaction((tx) => {
      tx.executeSql(
        "DELETE FROM contacts WHERE id = ?;",
        [id],
        () => router.back(),
        (trans, err) => {
          console.log(err);
        }
      );
    });
  }

  return (
    <PaperProvider>
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
          label="Email"
          value={formData.email}
          style={styles.textInput}
          onChangeText={(text) => handleChange("email", text)}
        />
        <TextInput
          label="Productos y servicios"
          value={formData.products_services}
          style={styles.textInput}
          onChangeText={(text) => handleChange("products_services", text)}
        />
        <SegmentedButtons
          value={formData.classification}
          style={{
            marginVertical: 10
          }}
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
            onPress={() => setdeleteDialogShow(true)}
          >
            Eliminar
          </Button>
          <Portal>
            <Dialog
              visible={deleteDialogShow}
              onDismiss={() => setdeleteDialogShow(false)}
            >
              <Dialog.Title>¿Estás seguro de eliminar esta compañia?</Dialog.Title>
              <Dialog.Content>
                <Text variant="bodyMedium">Se eliminara permanentemente.</Text>
              </Dialog.Content>
              <Dialog.Actions>
                <Button onPress={() => setdeleteDialogShow(false)}>
                  Cancelar
                </Button>
                <Button onPress={deleteCompany} textColor="red">
                  Eliminar
                </Button>
              </Dialog.Actions>
            </Dialog>
          </Portal>
        </View>
      </View>
    </PaperProvider>
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
