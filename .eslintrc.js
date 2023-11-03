module.exports = {
  root: true,
  extends: [
    "universe/native",
    "universe/shared/typescript-analysis",
    "prettier",
  ],
  ignorePatterns: ["build", "*.config.js"],
  overrides: [
    {
      files: ["*.ts", "*.tsx", "*.d.ts"],
      parserOptions: {
        project: ["./tsconfig.json", "./example/tsconfig.json"],
      },
    },
  ],
  plugins: ["prettier"],
  rules: {
    "no-var": ["error"],
  },
};
