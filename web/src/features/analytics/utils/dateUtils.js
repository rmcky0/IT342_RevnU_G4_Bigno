export const getFormattedDate = () => {
  const date = new Date();
  return date.toLocaleDateString("en-GB", {
    weekday: "long",
    day: "numeric",
    month: "long",
  });
};
