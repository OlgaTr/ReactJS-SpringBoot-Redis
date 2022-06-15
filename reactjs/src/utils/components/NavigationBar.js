import React from "react";
import {useDispatch, useSelector} from "react-redux";
import {logOut} from "../../app/userSlice";
import {useNavigate} from "react-router-dom";
import {clearAll} from "../../app/coffeeDrinksSlice";

export default function NavigationBar() {

    const dispatch = useDispatch();
    const navigate = useNavigate();
    const isAuthenticated = useSelector(state => state.justCoffee.user.isAuthenticated);
    const username = useSelector(state => state.justCoffee.user.username);

    function handleLogOut() {
        dispatch(logOut());
        dispatch(clearAll());
        navigate('/');
    }

    if (!isAuthenticated) {
        return (
            <div className='navbar'>
                <div className='nav-menu'>
                    <a href='/'>Menu</a>
                </div>
                <div className='nav-join'>
                    <a href='/register'>Join now</a>
                </div>
                <div className='nav-sign-in'>
                    <a href='/login'>Sign in</a>
                </div>
            </div>
        );
    } else {
        return (
            <div className='navbar'>
                <div className='nav-menu'>
                    <a href='/'>Menu</a>
                </div>
                <button onClick={() => handleLogOut()} className='nav-log-out'>Log out</button>
            </div>
        );
    }
}