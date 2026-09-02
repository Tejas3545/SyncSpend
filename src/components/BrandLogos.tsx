import React from 'react';

interface BrandLogoProps {
  className?: string;
  size?: number;
}

/**
 * Official Pixel-Perfect Google "G" 4-Color Logo
 */
export const GoogleLogo: React.FC<BrandLogoProps> = ({ className = 'w-5 h-5', size }) => {
  return (
    <svg
      viewBox="0 0 24 24"
      width={size || 24}
      height={size || 24}
      className={className}
      xmlns="http://www.w3.org/2000/svg"
      aria-label="Google"
    >
      <path
        d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
        fill="#4285F4"
      />
      <path
        d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
        fill="#34A853"
      />
      <path
        d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.06H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.94l2.85-2.22.81-.63z"
        fill="#FBBC05"
      />
      <path
        d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.06l3.66 2.84c.87-2.6 3.3-4.52 6.16-4.52z"
        fill="#EA4335"
      />
    </svg>
  );
};

/**
 * Official Pixel-Perfect Notion Logo (Iconic 3D N Cube)
 */
export const NotionLogo: React.FC<BrandLogoProps> = ({ className = 'w-5 h-5', size }) => {
  return (
    <svg
      viewBox="0 0 100 100"
      width={size || 24}
      height={size || 24}
      className={className}
      fill="currentColor"
      xmlns="http://www.w3.org/2000/svg"
      aria-label="Notion"
    >
      <path d="M6.017 11.28l58.11-6.103c8.118-.853 11.968 1.493 16.236 5.548l13.89 13.435c2.986 2.986 4.693 6.612 4.693 12.373v51.625c0 6.613-2.773 10.453-9.387 11.093l-59.605 5.76c-4.48.427-8.32-.427-11.52-3.84L4.31 87.054c-2.987-3.2-4.267-6.827-4.267-11.733V21.734c0-5.76 2.774-9.814 5.974-10.454zm59.605 9.173L21.803 25.147c-1.494.213-2.134.853-2.134 2.133v46.72c0 2.133 1.067 3.2 2.774 3.2l44.585-4.48c1.493-.213 2.133-.853 2.133-2.133V23.653c0-2.133-1.066-3.2-3.546-3.2zm-28.8 15.147l18.56-1.92c.64 0 1.28.427 1.493 1.28l.854 3.84h-.427c-1.494-.64-3.414-.853-4.694-.64l-9.173.853v24.533l7.68-.64c2.133-.213 3.627-.853 4.267-1.92.427-.64.64-1.707.64-3.2h.853l-.64 9.173h-.853c-.214-1.28-.64-2.133-1.494-2.56-.64-.427-2.133-.427-4.053-.213l-6.4.64v5.333c0 2.134.426 3.414 1.28 3.84.853.427 2.346.64 4.48.427l10.88-1.067c2.773-.427 4.906-2.133 6.186-4.907l1.067-2.346h.853l-1.92 8.746-24.96 2.347c-1.066.213-1.92-.214-2.346-.854-.427-.64-.64-1.706-.64-3.413V38.16c0-1.707.426-2.773 1.066-3.2.854-.427 2.134-.64 3.627-.64z" />
    </svg>
  );
};

/**
 * Official Google Sheets App Icon (Green with white grid)
 */
export const GoogleSheetsAppIcon: React.FC<BrandLogoProps> = ({ className = 'w-6 h-6', size }) => {
  return (
    <svg
      viewBox="0 0 48 48"
      width={size || 24}
      height={size || 24}
      className={className}
      xmlns="http://www.w3.org/2000/svg"
      aria-label="Google Sheets"
    >
      <path fill="#0F9D58" d="M37 45H11c-2.2 0-4-1.8-4-4V7c0-2.2 1.8-4 4-4h18l12 12v26c0 2.2-1.8 4-4 4z" />
      <path fill="#87CEAC" d="M29 3v12h12" />
      <path fill="#F1F1F1" d="M14 21h20v18H14z" />
      <path fill="#0F9D58" d="M22 23h10v3H22zm0 5h10v3H22zm0 5h10v3H22zM16 23h4v3h-4zm0 5h4v3h-4zm0 5h4v3h-4z" />
    </svg>
  );
};

/**
 * Android Robot Logo
 */
export const AndroidLogo: React.FC<BrandLogoProps> = ({ className = 'w-4 h-4', size }) => {
  return (
    <svg
      viewBox="0 0 24 24"
      width={size || 16}
      height={size || 16}
      className={className}
      fill="currentColor"
      xmlns="http://www.w3.org/2000/svg"
      aria-label="Android"
    >
      <path d="M17.523 15.3414c-.5511 0-.9993-.4486-.9993-.9997s.4482-.9993.9993-.9993c.551 0 .9993.4482.9993.9993.0001.5511-.4483.9997-.9993.9997m-11.046 0c-.5511 0-.9993-.4486-.9993-.9997s.4482-.9993.9993-.9993c.5511 0 .9993.4482.9993.9993 0 .5511-.4482.9997-.9993.9997m11.4045-6.02l1.9973-3.4592a.416.416 0 00-.1521-.5676.416.416 0 00-.5676.1521l-2.0223 3.503C15.5867 8.358 13.856 8 12 8s-3.5867.358-5.1368.9507L4.8409 5.4477a.4161.4161 0 00-.5677-.1521.4157.4157 0 00-.1521.5676l1.9973 3.4592C2.6889 11.1867 1.4 14.3314 1.4 17.8864H22.6c0-3.555-1.2889-6.6997-4.7185-8.565" />
    </svg>
  );
};
