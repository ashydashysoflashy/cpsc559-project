// eslint.config.js
import js from '@eslint/js'
import globals from 'globals'
import importPlugin from 'eslint-plugin-import'
import reactHooks from 'eslint-plugin-react-hooks'
import tsPlugin from '@typescript-eslint/eslint-plugin'
import tsParser from '@typescript-eslint/parser'
import reactRefresh from 'eslint-plugin-react-refresh'

export default [
  {
    ignores: ['dist', 'node_modules'],
  },
  {
    files: ['**/*.{ts,tsx}'],
    languageOptions: {
      parser: tsParser,
      parserOptions: {
        ecmaVersion: 2020,
      },
      globals: {
        ...globals.browser,
      },
    },
    plugins: {
      import: importPlugin,
      '@typescript-eslint': tsPlugin,
      'react-hooks': reactHooks,
      'react-refresh': reactRefresh
    },
    rules: {
      // Merge recommended sets from
      ...js.configs.recommended.rules,
      ...tsPlugin.configs.recommended.rules,
      ...reactHooks.configs.recommended.rules,

      // File resolution checks
      'import/no-unresolved': [
        'error',
        {
          ignore: ['^/'], // ignore any path that begins with '/'
        },
      ],
      'import/named': 'error',

      // Ignore unused variables named '_'
      '@typescript-eslint/no-unused-vars': ['warn', { argsIgnorePattern: '^_' }],
      
      // Allow constant exports
      'react-refresh/only-export-components': ['warn', { allowConstantExport: true }]

    },
    settings: {
      'import/resolver': {
        // So ESLint can resolve TS files
        typescript: {
          alwaysTryTypes: true,
          project: './tsconfig.json',
        },
      },
    },
  },
]
