import { registerRootComponent } from 'expo';

import App from './App';

// registerRootComponent cuida de registrar o app tanto no Expo Go quanto
// num build nativo (APK), preparando o ambiente automaticamente.
registerRootComponent(App);
