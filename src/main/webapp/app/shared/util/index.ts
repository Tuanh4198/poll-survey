export const generateAvatar = (displayName?: string) => {
  if (!displayName) return null;
  const names = displayName.split(' ');
  let avatarText = names[0].charAt(0).toUpperCase();
  if (names.length > 1) {
    avatarText += names[names.length - 1].charAt(0).toUpperCase();
  }
  return avatarText;
};
