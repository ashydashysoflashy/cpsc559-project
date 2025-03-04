# Client/Frontend Project

## Prerequisites

- **Node.js** v16+.
- **npm** for dependency management
- [Download both here](https://nodejs.org/en/download)

## Getting Started

1. **Install** dependencies. Inside /client run the following command:

```
npm install
```

2. **Start** the development server:

```
npm run dev
```

This will open the app at http://localhost:5173, which can be visited in your browser.

## Folder Structure

Here's a high level overview of our project layout

```
client
├─ public/
│ └─ (static assets)
├─ src/
│ ├─ pages/
│ │ ├─ Page/
│ │ │ ├─ Page.tsx
│ │ │ └─ Page.module.scss
│ │ │ ...
│ ├─ components/
│ │ ├─ Component/
│ │ │ ├─ Component.tsx
│ │ │ └─ Component.module.scss (optional)
│ │ │ ...
│ ├─ services/
│ │ ├─ api.ts
│ │ │ ...
│ ├─ styles/
│ │ ├─ \_variables.scss
│ │ └─ global.scss
│ ├─ App.tsx
│ ├─ main.tsx
│ └─ vite-env.d.ts
├─ .eslintrc.js (or eslint.config.js)
├─ .prettierrc
├─ tsconfig.json
├─ package.json
└─ vite.config.ts
```

- **/pages**: Screens (pages) corresponding to routes. Often have corresponding **.scss** file for styling.
- **/components**: Reusable or route-agnostic components (buttons, toggles, etc.). Often have corresponding **.scss** file for styling.
- **/services**: Contains **api.ts** (our Axios instance for making HTTP requests) + any other special service files if needed.
- **/styles**: Contains **global.scss** which is used for anything to be applied to entire app, and **\_variables.scss** which can contain styling variables to be reused.
- **App.tsx**: Top-level router with route definitions.
- **main.tsx**: App entry point.

## Scripts

- **npm run dev**: Starts the development server at http://localhost:5173.
- **npm run build**: Builds for production (output in dist/).
- **npm run preview**: Locally preview the production build.
- **npm run lint**: Runs ESLint checks.
- **npm run format** (if configured with Prettier): Formats your code based on .prettierrc.
