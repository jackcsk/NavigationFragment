package com.dmcapps.navigationfragment.common.core;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.dmcapps.navigationfragment.common.interfaces.Lifecycle;
import com.dmcapps.navigationfragment.common.interfaces.Navigation;
import com.dmcapps.navigationfragment.common.interfaces.NavigationManagerContainer;
import com.dmcapps.navigationfragment.common.interfaces.NavigationManagerListener;
import com.dmcapps.navigationfragment.common.interfaces.Stack;
import com.dmcapps.navigationfragment.common.interfaces.State;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dcarmo on 2016-12-18.
 */

public class NavigationManager implements Serializable {
    private static final String TAG = NavigationManager.class.getSimpleName();

    private transient NavigationManagerListener mListener;
    private transient WeakReference<NavigationManagerContainer> mContainer;

    // I don't like this ... but I'm having trouble thinking of a way to get rid of it ...
    private transient List<Navigation> mInitialNavigation;

    private NavigationTransaction.Builder mNextTransaction;

    private NavigationConfig mNavigationConfig;

    private Lifecycle mLifecycle;
    private State mState;
    private Stack mStack;

    public NavigationManager() { }

    public void setNavigationListener(NavigationManagerListener listener) {
        mListener = listener;
    }

    public void addInitialNavigation(Navigation navigation) {
        if (mInitialNavigation == null) {
            mInitialNavigation = new ArrayList<>();
        }
        mInitialNavigation.add(navigation);
    }

    public List<Navigation> getInitialNavigation() {
        if (mInitialNavigation == null) {
            mInitialNavigation = new ArrayList<>();
        }
        return mInitialNavigation;
    }

    public void nullifyInitialNavigation() {
        mInitialNavigation = null;
    }

    public void setContainer(NavigationManagerContainer container) {
        mContainer = new WeakReference<>(container);
    }

    public NavigationManagerContainer getContainer() {
        if (mContainer == null || mContainer.get() == null) {
            return null;
        }
        return mContainer.get();
    }

    public void setStack(Stack stack) {
        mStack = stack;
    }

    public Stack getStack() {
        return mStack;
    }

    public void setNavigationConfig(NavigationConfig config) {
        mNavigationConfig = config;
    }

    public NavigationConfig getConfig() {
        return mNavigationConfig;
    }

    public void setState(State state) {
        mState = state;
    }

    public State getState() {
        return mState;
    }

    public void setLifecycle(Lifecycle lifecycle) {
        mLifecycle = lifecycle;
    }

    public Lifecycle getLifecycle() {
        return mLifecycle;
    }

    public void beginTransaction() {
        mNextTransaction = NavigationTransaction.withConfig(mNavigationConfig);
    }

    public NavigationManager setNavBundle(Bundle bundle) {
        mNextTransaction.setNavBundle(bundle);
        return this;
    }

    public NavigationManager setAnimations(Integer presentIn, Integer presentOut, Integer dismissIn, Integer dismissOut) {
        return setPresentAnim(presentIn, presentOut).setDismissAnim(dismissIn, dismissOut);
    }

    public NavigationManager setPresentAnim(Integer in, Integer out) {
        mNextTransaction.setPresentInAnim(in)
            .setPresentOutAnim(out);
        return this;
    }

    public NavigationManager setDismissAnim(Integer in, Integer out) {
        mNextTransaction.setDismissInAnim(in)
                .setDismissOutAnim(out);
        return this;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public NavigationManager addSharedElement(View view, String name) {
        mNextTransaction.addSharedElement(view, name);
        return this;
    }

    /**
     * Overrides the default present animations for all present actions on the fragment manager.
     *
     * @param
     *      animIn -> Present animation in
     * @param
     *      animOut -> Present animation out
     */
    public void setDefaultPresentAnimations(int animIn, int animOut) {
        mNavigationConfig = mNavigationConfig.withPresentAnimations(animIn, animOut);
    }

    /**
     * Overrides the default dismiss animations for all dismiss actions on the fragment manager.
     *
     * @param
     *      animIn -> Dismiss animation in
     * @param
     *      animOut -> Dismiss animation out
     */
    public void setDefaultDismissAnimations(int animIn, int animOut) {
        mNavigationConfig = mNavigationConfig.withDismissAnimations(animIn, animOut);
    }

    /**
     * Push a new Fragment onto the stack and presenting it to the screen
     * Uses default animation of slide in from right and slide out to left.
     *
     * @param
     *      navFragment -> The Fragment to show. It must be a Fragment that implements {@link Navigation}
     */
    public void presentFragment(Navigation navFragment) {
        if (mNextTransaction == null) {
            beginTransaction();
        }
        mStack.pushFragment(this, navFragment, mNextTransaction.build());
    }

    /**
     * Push a new Fragment onto the stack and presenting it to the screen
     * Uses default animation of slide in from right and slide out to left.
     *
     * @param
     *      navFragment -> The Fragment to show. It must be a Fragment that implements {@link Navigation}
     * @param
     *      navBundle -> The navigation bundle to add to the fragment being pushed
     *
     * @deprecated
     *      This function is being replaced with the {@link NavigationManager#setNavBundle(Bundle)} method call.
     *      In order to add a bundle you should call
     *      <code>
     *          getNavigationManager()
     *              .setNavBundle(navBundle)
     *              .presentFragment(navFragment);
     *      </code>
     *      Allowing for more parameters to be passed in with the call.
     *      To be removed in 2.1.0.
     */
    @Deprecated
    public void presentFragment(Navigation navFragment, Bundle navBundle) {
        mNextTransaction.setNavBundle(navBundle);
        presentFragment(navFragment);
    }

    /**
     * Pop the current fragment off the top of the stack and dismiss it.
     * Uses default animation of slide in from left and slide out to right animation.
     */
    public void dismissFragment() {
        mStack.popFragment(this, mNextTransaction.build());

        if (mListener != null) {
            mListener.didDismissFragment();
        }
    }

    /**
     * Pop the current fragment off the top of the stack and dismiss it.
     * Uses default animation of slide in from left and slide out to right animation.
     *
     * @param
     *      navBundle -> The navigation bundle to add to the fragment after the pop occurs
     */
    public void dismissFragment(Bundle navBundle) {
        mNextTransaction.setNavBundle(navBundle);
        dismissFragment();
    }

    /**
     * Adds the fragment to the current stack.
     *
     * @param
     *      navFragment -> The navigation fragment to be added to the stack.
     */
    void addToStack(Navigation navFragment) {
        mState.getStack().add(navFragment.getNavTag());
    }

    /**
     * Access the fragment that is on the top of the navigation stack.
     *
     * @return
     *      {@link Navigation} that is on the top of the stack.
     */
    public Navigation getTopFragment() {
        if (mState.getStack().size() > 0) {
            return getFragmentAtIndex(mState.getStack().size() - 1);
        }
        else {
            Log.e(TAG, "No fragments in the navigation stack, returning null.");
            return null;
        }
    }

    /**
     * Returns the fragment at the 0 index.
     *
     * @return
     *      {@link Navigation} at the 0 index if available.
     */
    public Navigation getRootFragment() {
        return getFragmentAtIndex(0);
    }

    /**
     * Access the fragment at the given index of the navigation stack.
     *
     * @return
     *      {@link Navigation} that is on the top of the stack.
     */
    public Navigation getFragmentAtIndex(int index) {
        if (mState.getStack().size() > index) {
            return mStack.getFragmentAtIndex(this, index);
        }
        else {
            Log.e(TAG, "No fragment at that position in the navigation stack, returning null. (Stack size: " + mState.getStack().size() + ". Index attempted: " + index + ".");
            return null;
        }
    }

    /**
     * Remove the {@link Navigation} that is on the top of the stack.
     *
     * @return
     *      true -> A {@link Navigation} has been removed
     *      false -> No fragment has been removed because we are at the bottom of the stack for that stack.
     */
    public boolean onBackPressed() {
        if (mState.getStack().size() > mNavigationConfig.getMinStackSize()) {
            dismissFragment();
            return true;
        }

        return false;
    }

    /**
     * Remove all fragments from the stack including the Root. The add the given {@link Navigation}
     * as the new root fragment. The definition of the Root Fragment is the Fragment at the min stack size position.
     *
     * @param
     *      navFragment -> The fragment that you would like as the new Root of the stack.
     */
    public void replaceRootFragment(Navigation navFragment) {
        clearNavigationStackToIndex(mNavigationConfig.getMinStackSize() - 1, true);
        presentFragment(navFragment);
    }

    /**
     * Remove all fragments from the stack until we reach the Root Fragment (the fragment at the min stack size)
     */
    public void clearNavigationStackToRoot() {
        clearNavigationStackToIndex(mNavigationConfig.getMinStackSize());
    }

    /**
     * Remove all fragments up until the given position.
     *
     * @param
     *      stackPosition -> The position (0 indexed) that you would like to pop to.
     */
    public void clearNavigationStackToIndex(int stackPosition) {
        clearNavigationStackToIndex(stackPosition, false);
    }

    /**
     * Remove all fragments up until the given position.
     *
     * @param
     *      index -> The index (0 based) that you would like to pop to.
     */
    public void clearNavigationStackToIndex(int index, boolean inclusive) {
        mStack.clearNavigationStackToIndex(this, index, inclusive);
    }

    /**
     * Check if the current top fragment is the root fragment
     *
     * @return
     *      true -> Stack is currently at the root fragment
     *      false -> Stack is not at the root fragment
     */
    public boolean isOnRootFragment() {
        return mState.getStack().size() == mNavigationConfig.getMinStackSize();
    }

    /**
     * Returns the {@link NavigationManager} stack size. A stack size of 0 represents empty.
     *
     * @return
     *      The current stack size.
     */
    public int getCurrentStackSize() {
        return mState.getStack().size();
    }

    // ===============================
    // START DEVICE STATE METHODS
    // ===============================

    public boolean isPortrait() {
        return mState.isPortrait();
    }

    public boolean isTablet() {
        return mState.isTablet();
    }

    // ===============================
    // END DEVICE STATE METHODS
    // ===============================

}
