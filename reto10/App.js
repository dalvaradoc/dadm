import { createRef, useEffect, useState } from "react";
import { Button, Pressable, ScrollView } from "react-native";
import { FlatList } from "react-native";
import { StyleSheet, Text, View } from "react-native";
import {
  Divider,
  List,
  Modal,
  PaperProvider,
  Portal,
} from "react-native-paper";

const API_URL = "https://www.datos.gov.co/resource/d5xq-sqc2.json?$limit=50";
const pageSize = 50;

const columns = [
  "programa",
  "tipo",
  "vigencia",
  "genero",
  "estado_civil",
  "nacionalidad",
  "depa_pro_colegio",
  "ciudad_pro_colegio",
  "depa_resi",
  "ciudad_resi",
  "facultad",
  "modalidad",
  "metodologia",
  "jornada",
  "sede",
  "estrato",
];

export default function App() {
  const [data, setData] = useState(null);
  const [visible, setVisible] = useState(false);
  const [filters, setFilters] = useState({});
  const [filtersOptions, setFiltersOptions] = useState({});
  const [rowsCount, setRowsCount] = useState(0);
  const [offset, setOffset] = useState(0);
  const [url, setUrl] = useState(API_URL);
  const [loading, setLoading] = useState(true);

  flatList = createRef();

  const showModal = () => setVisible(true);
  const hideModal = () => setVisible(false);

  useEffect(() => {
    columns.map((col) => {
      fetch(API_URL + "&$select=DISTINCT%20" + col + "&$order=" + col)
        .then((res) => res.json())
        .then((resj) => {
          let temp = filtersOptions;
          temp[col] = resj;
          setFiltersOptions(temp);
        });
    });
    fetch(API_URL + "&$select=COUNT(*)%20as%20count")
      .then((res) => res.json())
      .then((resj) => {
        setRowsCount(resj[0]["count"]);
      });
    fetch(API_URL)
      .then((res) => res.json())
      .then((resj) => {
        for (var i = 0; i < resj.length; ++i) {
          resj[i]["id"] = i;
        }
        setData(resj);
        setLoading(false);
      });
  }, []);

  useEffect(() => {
    fetch(url + "&$select=COUNT(*)%20as%20count")
      .then((res) => res.json())
      .then((resj) => {
        setRowsCount(resj[0]["count"]);
      });
    fetch(url + "&$offset=" + offset * pageSize)
      .then((res) => res.json())
      .then((resj) => {
        for (var i = 0; i < resj.length; ++i) {
          resj[i]["id"] = i;
        }
        setData(resj);
        setLoading(false);
        if (!data.length) setRowsCount(0);
      });
  }, [offset, url]);

  const B = (props) => (
    <Text style={{ fontWeight: "bold" }}>{props.children}</Text>
  );

  const addFilter = (col, filter) => {
    // let temp = JSON.parse(JSON.stringify(filters));
    setFilters({
      ...filters,
      [col]: filter,
    });
  };

  const cleanFilters = () => {
    if (!filters) return;
    setUrl(API_URL);
    setFilters({});
    setOffset(0);
  };

  const applyFilters = () => {
    if (!filters) {
      hideModal();
      return;
    }
    setLoading(true);
    let temp = API_URL;
    for (let key in filters) {
      if (!filters[key]) continue;
      temp = temp + "&" + key + "=" + filters[key];
    }
    console.log(url);
    hideModal();
    setOffset(0);
    setUrl(temp);
  };

  const Filters = () => {
    return (
      <ScrollView
        style={{
          padding: 15,
        }}
      >
        <View
          style={{
            flexDirection: "row",
            width: "100%",
            marginBottom: 15,
          }}
        >
          <Pressable
            style={{
              backgroundColor: "#BA1200",
              padding: 10,
              width: "50%",
              alignItems: "center",
            }}
            onPress={cleanFilters}
          >
            <Text
              style={{
                fontWeight: "bold",
                color: "white",
              }}
            >
              LIMPIAR
            </Text>
          </Pressable>
          <Pressable
            style={{
              backgroundColor: "#216869",
              padding: 10,
              width: "50%",
              alignItems: "center",
            }}
            onPress={applyFilters}
          >
            <Text
              style={{
                fontWeight: "bold",
                color: "white",
              }}
            >
              FILTRAR
            </Text>
          </Pressable>
        </View>
        {columns.map((col) => {
          return (
            <View key={col}>
              <B>{col}</B>
              <List.Accordion
                title={filters[col] == null ? "NINGUNO" : filters[col]}
                style={{
                  backgroundColor: "#00000005",
                  marginTop: 5,
                }}
              >
                {filtersOptions[col].map((fopts) => {
                  return (
                    <List.Item
                      key={fopts[col]}
                      onPress={() => addFilter(col, fopts[col])}
                      title={fopts[col]}
                      style={{
                        backgroundColor: "#00000011",
                      }}
                    />
                  );
                })}
                <List.Item
                  onPress={() => addFilter(col, null)}
                  title="NINGUNO"
                />
              </List.Accordion>
            </View>
          );
        })}
      </ScrollView>
    );
  };

  const renderItem = ({ item }) => {
    return (
      <View
        style={{
          padding: 25,
          margin: 10,
          backgroundColor: "#FFF",
          borderRadius: 10,
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
        {columns.map((col) => {
          return (
            <View key={col} style={{ marginBottom: 5 }}>
              <Text>
                <B>{col}:</B> {item[col]}
              </Text>
            </View>
          );
        })}
      </View>
    );
  };

  const nextPage = () => {
    if ((offset+1) * pageSize > rowsCount) return;
    setLoading(true);
    setOffset(min(offset + 1, rowsCount / pageSize));
    if (data.length > 0) {
      this.flatList.current.scrollToIndex({
        index: 0,
        animated: false,
      });
    }
  };

  const previousPage = () => {
    if (offset == 0) return;
    setLoading(true);
    setOffset(offset - 1);
    if (data.length > 0) {
      this.flatList.current.scrollToIndex({
        index: 0,
        animated: false,
      });
    }
  };

  function min(a, b) {
    return Math.min(a, b);
  }

  function max(a, b) {
    return Math.max(a, b);
  }

  if (!data) return <Text>No se encontró información...</Text>;
  return (
    <PaperProvider>
      <View style={styles.container}>
        <Button title="Filtrar" onPress={showModal} />
        <Portal>
          <Modal
            visible={visible}
            onDismiss={hideModal}
            contentContainerStyle={{
              // padding: 20,
              marginHorizontal: 20,
              marginVertical: 50,
              backgroundColor: "white",
            }}
          >
            <Filters />
          </Modal>
        </Portal>
        {loading ? (
          <View
            style={{
              flexGrow: 1,
              alignItems: "center",
              justifyContent: "center",
            }}
          >
            <Text
              style={{
                fontSize: 20,
              }}
            >
              Loading...
            </Text>
          </View>
        ) : data.length > 0 ? (
          <FlatList
            data={data}
            renderItem={renderItem}
            windowSize={5}
            keyExtractor={(item) => item.id}
            extraData={data}
            ref={this.flatList}
          />
        ) : (
          <View
            style={{
              flexGrow: 1,
              alignItems: "center",
              justifyContent: "center",
            }}
          >
            <Text
              style={{
                fontSize: 20,
              }}
            >
              No se encontraron
            </Text>
          </View>
        )}
        <View
          style={{
            flexDirection: "row",
            // backgroundColor: "#2196F3",
            backgroundColor: "black",
          }}
        >
          <Pressable
            style={{
              flexGrow: 1,
            }}
            onPress={previousPage}
          >
            <Text
              style={{
                padding: 10,
                color: offset == 0 ? "grey" : "white",
                fontWeight: "bold",
              }}
            >
              ATRÁS
            </Text>
          </Pressable>
          <View
            style={{
              alignItems: "center",
            }}
          >
            <Text
              style={{
                color: "white",
              }}
            >
              {min(offset * pageSize, rowsCount)} -{" "}
              {min((offset + 1) * pageSize, rowsCount)}
            </Text>
            <Text
              style={{
                color: "white",
              }}
            >
              Total {rowsCount}
            </Text>
          </View>
          <Pressable
            style={{
              flexGrow: 1,
              alignItems: "flex-end",
            }}
            onPress={nextPage}
          >
            <Text
              style={{
                padding: 10,
                color: (offset+1)*pageSize > rowsCount ? "grey" : "white",
                fontWeight: "bold",
              }}
            >
              SIGUIENTE
            </Text>
          </Pressable>
        </View>
      </View>
    </PaperProvider>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    paddingTop: 25,
    backgroundColor: "#EAEAEA",
  },
});
