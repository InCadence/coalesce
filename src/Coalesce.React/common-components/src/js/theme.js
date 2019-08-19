import getMuiTheme from 'material-ui/styles/getMuiTheme';
import * as Colors from 'material-ui/styles/colors';

export function getDefaultTheme() {
  return getMuiTheme({
    palette: {
      primary1Color: Colors.grey500,
      accent1Color: Colors.yellow800,
      primary2Color: Colors.grey700,
      pickerHeaderColor: Colors.cyan800
    },
  });
}
