import { StyleSheet } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { Slot } from 'expo-router';


export default function Layout() {
  return (
    <SafeAreaView style={styles.container}>
      <Slot />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    backgroundColor: "#fff",
    flex: 1,
  },
});
