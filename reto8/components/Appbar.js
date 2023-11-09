import { Link } from 'expo-router';
import * as React from 'react';
import { Appbar } from 'react-native-paper';


const AppBar = ({title, backUrl = '' }) => (
  <Appbar.Header>
    <Link href={backUrl} asChild>
        <Appbar.BackAction onPress={() => {}} />
    </Link>
    <Appbar.Content title={title} />
  </Appbar.Header>
);

export default AppBar;