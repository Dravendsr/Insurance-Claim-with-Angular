import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const userJson = localStorage.getItem('currentUser');
  
  if (userJson) {
    const user = JSON.parse(userJson);
    
    // Ensure password exists (set during login) to avoid 'undefined' credentials
    const password = user.password || ''; 
    const credentials = btoa(`${user.username}:${password}`);
    
    const authReq = req.clone({
      setHeaders: {
        // Ensure the Basic Auth credentials are correctly formatted
        Authorization: `Basic ${credentials}`,
        'Content-Type': 'application/json'
      }
    });
    
    return next(authReq);
  }
  
  return next(req);
};